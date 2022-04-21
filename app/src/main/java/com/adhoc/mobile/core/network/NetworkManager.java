package com.adhoc.mobile.core.network;

import android.content.Context;
import android.widget.Toast;

import com.adhoc.mobile.core.datalink.AdhocDevice;
import com.adhoc.mobile.core.datalink.DataLinkCallbacks;
import com.adhoc.mobile.core.datalink.DataLinkManager;

import java.util.ArrayList;
import java.util.List;
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
            adhocDeviceList.add(adhocDevice);
            callbacks.onConnectionSucceed(adhocDevice);
        }

        @Override
        public void onDisconnected(String endpointId) {
            adhocDeviceList = adhocDeviceList.stream()
                    .filter(e -> !e.getId().equals(endpointId)).collect(Collectors.toList());
            callbacks.onDisconnected(endpointId);
        }

        @Override
        public void onPayloadReceived(String message) {
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
        Toast.makeText(context, "Received: " + dataMessage.getPayload(), Toast.LENGTH_SHORT).show();
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
        // TODO(Look in Routing table)

        /**
         * if(destination is direct neighbor) {
         *      1. create data object
         *      2. send data object to datalink
         * } else if (destination is in routing table) {
         *      1. Get the next hop from routing table.
         *      2. form a data object with the next hop as destinaion.
         *      3. send data to datalink.
         * } else {
         *      // if we are here, it means that we don't know the path to the destination.
         *      // So we have to initiate a RREQ and wait for RREP, then send DATA message.
         *      1. Build a RREQ and broadcast it to all neighbors.
         *      2.
         * }
         *
         */

        String destinationId = destination.getId();

        DataMessage dataMessage = new DataMessage(destination.getId(), message);
        if (dataLinkManager.isDirectNeighbors(destination.getId())) {
            dataLinkManager.sendMessage(dataMessage.toJsonString(), destination.getId());
        } else if (aovdManager.knowNextHop(destinationId)) {
            String nextHop = aovdManager.getNextHop(destinationId);

            dataLinkManager.sendMessage(dataMessage.toJsonString(), nextHop);
        } else {
            // TODO (Broadcast RREQ) rec RREP -> Routing table
            dataLinkManager.broadcast(dataMessage.toJsonString());
        }

    }


    public void leaveNetwork() {
        dataLinkManager.leaveNetwork();
    }
}
