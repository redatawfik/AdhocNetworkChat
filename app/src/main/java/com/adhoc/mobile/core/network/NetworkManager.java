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
    private final AodvManager aodvManager;
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
        this.aodvManager = new AodvManager();
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
        if (aodvManager.isReceivedBefore(rreq.getBroadcastId(), rreq.getSourceId())) {
            // Discard the message as it is received before
            Log.i(TAG, "Discard the received before RREQ message=" + rreq);
            return;
        }

        rreq.incrementHopCount();

        if (rreq.getDestinationId().equals(myDevice.getId())) {
            // This node is the destination
            // TODO(Send RREP message to the originator source of the RREQ)

            Log.i(TAG, "This node is the destination for RREQ=" + rreq);
            // Save the destination sequence number into a hashmap
            aodvManager.saveDestSequenceNumber(rreq.getSourceId(), rreq.getSourceSequenceNumber());

            // Update routing table
            aodvManager.addRoute(rreq.getSourceId(), rreq.getGatewayId() /* interface id*/, rreq.getHopCount(),
                    rreq.getSourceSequenceNumber(), Constants.NO_LIFE_TIME, null);

//            if (rreq.getDestinationSequenceNumber() > aodvManager.getOwnSequenceNum()) {
//                aodvManager.getNextSequenceNumber();
//            }

            // Generate route reply
            RREP rrep = new RREP(rreq.getSourceId(), myDevice.getId(), aodvManager.getNextOwnSequenceNumber(),
                    rreq.getHopCount(), Constants.LIFE_TIME);


            send(rrep, rreq.getSourceId());

        } else if (aodvManager.containsDestination(rreq.getDestinationId())) {
            // TODO(Send a RREP message to the originator of the RREQ and to the destination)
        } else {
            // TODO(Continue the flooding by broadcasting the RREQ to the neighbours)

        }
    }

    private void processRREP(RREP rrep) {
    }

    private void processRERR(RERR rerr) {
        //remove unreachable from routing table
        aodvManager.removeRouteForDestination(rerr.getUnreachableDestinationId());
        Log.e(TAG, "Node Unreachable: " + rerr.getUnreachableDestinationId());
        rerr.addToSeq(Long.parseLong(rerr.getUnreachableDestinationId()));
    }

    public void joinNetwork() {
        dataLinkManager.joinNetwork();
    }

    public void send(AdhocMessage message, String destinationId) {

        if (dataLinkManager.isDirectNeighbors(destinationId)) {
            dataLinkManager.sendMessage(message, destinationId);
        } else if (aodvManager.containsDestination(destinationId)) {
            String nextHop = aodvManager.getNextHopId(destinationId);

            dataLinkManager.sendMessage(message, nextHop);
        } else {
            startTimerRREQ(destinationId, aodvManager.getNextOwnSequenceNumber(), Constants.RREQ_RETRIES,
                    Constants.NET_TRANVERSAL_TIME);
        }
    }

    public void sendMessage(String message, AdhocDevice destination) {
        Log.i(TAG, "Send message=" + message + " , to Device=" + destination);

        String destinationId = destination.getId();
        DataMessage dataMessage = new DataMessage(destination.getId(), myDevice.getId(), message);
        send(dataMessage, destinationId);
    }


    public void leaveNetwork() {
        dataLinkManager.leaveNetwork();
    }

    private void startTimerRREQ(String destinationId, long seqNumber, int retry, int time) {

        Log.i(TAG, "Broadcast RREQ to find a destinationId=" + destinationId);

        long broadcastId = aodvManager.getNextBroadcastId();
        RREQ rreqMessage = new RREQ(myDevice.getId(), destinationId, seqNumber,
                aodvManager.getDestSequenceNumber(destinationId), broadcastId, 0);

        // TODO(Add to broadcast id)
        aodvManager.addBroadcast(broadcastId, myDevice.getId());

        dataLinkManager.broadcast(rreqMessage);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Route route = aodvManager.getRouteForDest(destinationId);
                if (route == null && retry != 0) {
                    startTimerRREQ(destinationId, seqNumber, retry - 1, time * 2);
                    Log.i(TAG, "Broadcast retry=" + retry + " , with " + rreqMessage);
                } else if (route != null) {
                    // TODO(Send the message)
                }
            }
        }, time);
    }
}
