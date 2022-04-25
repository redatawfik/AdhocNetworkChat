package com.adhoc.mobile.core.network;

import android.content.Context;
import android.util.Log;

import com.adhoc.mobile.core.datalink.AdhocDevice;
import com.adhoc.mobile.core.datalink.DataLinkCallbacks;
import com.adhoc.mobile.core.datalink.DataLinkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class NetworkManager {

    private final String TAG = this.getClass().getName();

    private final DataLinkManager dataLinkManager;
    private final NetworkCallbacks callbacks;
    private final AdhocDevice myDevice;
    private final AovdManager aovdManager;
    private final Context context;


    private List<AdhocDevice> adhocDeviceList = new ArrayList<>();

    private final DataLinkCallbacks dataLinkCallbacks = new DataLinkCallbacks() {
        @Override
        public void onConnectionSucceed(AdhocDevice adhocDevice) {
            Log.i(TAG, "Connection succeed to device {}" + adhocDevice);
            adhocDeviceList.add(adhocDevice);
            callbacks.onConnectionSucceed(adhocDevice);
        }

        @Override
        public void onDisconnected(String endpointId) {
            Log.i(TAG, "Disconnect from device with id=" + endpointId);
            adhocDeviceList = adhocDeviceList.stream()
                    .filter(e -> !e.getId().equals(endpointId)).collect(Collectors.toList());
            callbacks.onDisconnected(endpointId);
        }

        @Override
        public void onPayloadReceived(String message) {
            Log.i(TAG, "Payload received" + message);
            processReceivedMessage(message);
        }
    };

    public NetworkManager(Context context, AdhocDevice device, NetworkCallbacks callbacks) {
        this.context = context;
        this.callbacks = callbacks;
        this.myDevice = device;
        this.aovdManager = new AovdManager();
        dataLinkManager = new DataLinkManager(context, device, dataLinkCallbacks);
    }

    private void processReceivedMessage(String message) {

        AdhocMessage adhocMessage = Utils.getObjectFromJson(message);

        switch (adhocMessage.getType()) {
            case DATA:
                processData((DataMessage) adhocMessage);
                break;
            case RREQ:
                processRREQ((RREQ) adhocMessage);
                break;
            case RREP:
                processRREP((RREP) adhocMessage);
                break;
            case RERR:
                processRERR((RERR) adhocMessage);
                break;
        }
    }

    private void processData(DataMessage dataMessage) {
        Log.i(TAG, "Received: " + dataMessage.getPayload() + " , for id=" + dataMessage.getDestinationId());
        if (dataMessage.getDestinationId().equals(myDevice.getId())) {
            Log.i(TAG, "Received message is for this device");
            callbacks.onPayloadReceived(dataMessage.getOriginId(), dataMessage.getDestinationId(), (String) dataMessage.getPayload());
        } else {
            Log.i(TAG, "Received message not for this device");
        }
    }

    private void processRREQ(RREQ rreq) {
        /*
        if(myId  = rreq.getDestiaionId){
            call applicationLayer
        } else (Routing table contins rreq.getdesId){
            Build RREP(
            Send rrep -> node received rreq
        } else {
        broadcase rreq
         */
    }

    private void processRREP(RREP rrep) {
    }

    private void processRERR(RERR rerr) {

    }

    public void joinNetwork() {
        dataLinkManager.joinNetwork();
    }

    public void sendMessage(String message, AdhocDevice destination) {
        Log.i(TAG, "Send message=" + message + " , to Device=" + destination);

        String destinationId = destination.getId();

        DataMessage dataMessage = new DataMessage(destination.getId(), myDevice.getId(), message);
        if (dataLinkManager.isDirectNeighbors(destination.getId())) {
            dataLinkManager.sendMessage(dataMessage.toJsonString(), destination.getId());
        } else if (aovdManager.knowNextHop(destinationId)) {
            String nextHop = aovdManager.getNextHop(destinationId);

            dataLinkManager.sendMessage(dataMessage.toJsonString(), nextHop);
        } else {
            dataLinkManager.broadcast(dataMessage.toJsonString());
            startTimerRREQ(destinationId, aovdManager.getNextSequenceNumber(), Constants.RREQ_RETRIES,
                    Constants.NET_TRANVERSAL_TIME);
        }
    }


    public void leaveNetwork() {
        dataLinkManager.leaveNetwork();
    }

    private void startTimerRREQ(String destinationId, long seqNumber, int retry, int time) {

        Log.i(TAG, "Broadcast RREQ to find a destinationId=" + destinationId);

        RREQ rreqMessage = new RREQ(myDevice.getId(), destinationId, seqNumber,
                aovdManager.getDestSequenceNumber(destinationId), aovdManager.getNextBroadcastId(), 0);

        dataLinkManager.broadcast(rreqMessage.toJsonString());


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Route route = aovdManager.getRouteForDest(destinationId);
                if (route == null && retry != 0) {
                    startTimerRREQ(destinationId, seqNumber, retry - 1, time * 2);
                    Log.i(TAG, "Broadcast retry=" + retry + " , with " + rreqMessage);
                }
            }
        }, time);
    }
}
