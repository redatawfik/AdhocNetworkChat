package com.adhoc.mobile.core.network;

import static com.adhoc.mobile.core.network.Constants.HELLO_PROTOCOL_TIMER_INTERVAL;

import android.content.Context;
import android.util.Log;

import com.adhoc.mobile.core.datalink.AdhocDevice;
import com.adhoc.mobile.core.datalink.DataLinkCallbacks;
import com.adhoc.mobile.core.datalink.DataLinkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
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
    private final Map<String, Queue<DataMessage>> pendingMessages;


    private List<AdhocDevice> adhocDeviceList = new ArrayList<>();

    private final DataLinkCallbacks dataLinkCallbacks = new DataLinkCallbacks() {
        @Override
        public void onConnectionSucceed(AdhocDevice adhocDevice) {
            Log.i(TAG, "Connection succeed to device {}" + adhocDevice);
            adhocDeviceList.add(adhocDevice);
            callbacks.onConnectionSucceed(adhocDevice);
        }

        @Override
        public void onDisconnected(String publicId, String privateId) {
            Log.i(TAG, "Disconnect from device with id=" + publicId);
            adhocDeviceList = adhocDeviceList.stream()
                    .filter(e -> !e.getId().equals(publicId)).collect(Collectors.toList());
//            sendRERR(publicId, privateId);
            callbacks.onDisconnected(publicId);
        }

        @Override
        public void onPayloadReceived(String message, String endpointId) {
            Log.i(TAG, "Payload received" + message);
            processReceivedMessage(message, endpointId);
        }
    };

    public NetworkManager(Context context, AdhocDevice device, NetworkCallbacks callbacks) {
        this.context = context;
        this.callbacks = callbacks;
        this.myDevice = device;
        this.aodvManager = new AodvManager();
        dataLinkManager = new DataLinkManager(context, device, dataLinkCallbacks);
        pendingMessages = new HashMap<>();
    }

    private void sendRERR(String publicId, String privateId) {
        Log.i(TAG, "Send RERR to:" + privateId + "      -   " + publicId);

        // 1. Remove publicId from Routing Table
        aodvManager.removeRouteForDestination(publicId);

        // 2. Get precursors which have the privateId as nextHop
        List<Route> nodesList = aodvManager.getAndRemoveRoutesWithNextHop(privateId);

        for (Route route : nodesList) {
            Set<String> precursors = route.getPrecursors();
            for (String pre : precursors) {
                RERR rerr = new RERR(route.getDestinationId());
                dataLinkManager.sendDirect(rerr, pre);
            }
        }
    }

    private void processReceivedMessage(String message, String endpointId) {

        AdhocMessage adhocMessage = Utils.getObjectFromJson(message, endpointId);

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
//                processRERR((RERR) adhocMessage);
                break;
            case HELLO:
                Log.e(TAG, aodvManager.printRoutingTable());
                processHelloMessage((HelloMessage) adhocMessage);
                break;
        }
    }

    private void processHelloMessage(HelloMessage helloMessage) {
        AdhocDevice adhocDevice = helloMessage.getAdhocDevice();
        if (!aodvManager.isReceivedBefore(helloMessage.getBroadcastId(), adhocDevice.getId())) {
            aodvManager.addBroadcast(helloMessage.getBroadcastId(), adhocDevice.getId());
            callbacks.onConnectionSucceed(helloMessage.getAdhocDevice());
            dataLinkManager.broadcast(helloMessage);
        } else {
            Log.i(TAG, "Ignore the hello message, received before. Message=" + helloMessage);
        }
    }

    private void processData(DataMessage dataMessage) {
        Log.i(TAG, "Received: " + dataMessage + " , for id=" + dataMessage.getDestinationId());
        if (dataMessage.getDestinationId().equals(myDevice.getId())) {
            Log.i(TAG, "Received message is for this device");
            callbacks.onPayloadReceived(dataMessage.getOriginId(), dataMessage.getDestinationId(), (String) dataMessage.getPayload());
        } else if (aodvManager.containsDestination(dataMessage.getDestinationId())) {

            Route route = aodvManager.getRouteForDest(dataMessage.getDestinationId());
            Log.i(TAG, "Received data message is not for this device, forward to:" + route);
            route.updateTimeToLive();

            dataLinkManager.sendDirect(dataMessage, route.getNextHop());
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

        // TODO(Update the routing table only if the received seq number is higher)
        rreq.incrementHopCount();

        // This is the destination node for the RREQ.
        if (rreq.getDestinationId().equals(myDevice.getId())) {
            // This node is the destination
            Log.i(TAG, "This node is the destination for RREQ=" + rreq);
            // Save the destination sequence number into a hashmap
            aodvManager.saveDestSequenceNumber(rreq.getSourceId(), rreq.getSourceSequenceNumber());

            // Update or create a route in routing table.
            aodvManager.addRoute(rreq.getSourceId(), rreq.getGatewayId() /* interface id*/, rreq.getHopCount(),
                    rreq.getSourceSequenceNumber(), Constants.NO_LIFE_TIME, null);


            if (rreq.getDestinationSequenceNumber() > aodvManager.getOwnSequenceNum()) {
                aodvManager.getNextOwnSequenceNumber();
            }

            // Generate route reply
            RREP rrep = new RREP(rreq.getSourceId(), myDevice.getId(), aodvManager.getNextOwnSequenceNumber(),
                    rreq.getSourceSequenceNumber(), 0, Constants.LIFE_TIME);

            Log.i(TAG, "Send RREP=" + rrep);
            send(rrep, rreq.getSourceId());

            // The saved seq number has to be >= rreq.seqNumber
        } else if (aodvManager.containsDestination(rreq.getDestinationId()) &&
                aodvManager.getRouteForDest(rreq.getDestinationId()).getDestinationSequenceNumber() >=
                        rreq.getDestinationSequenceNumber()) {

            // TODO(Send a RREP message to the originator of the RREQ and to the destination)

            Log.i(TAG, "Found destination in Routing table for RREQ=" + rreq);
            Route route = aodvManager.getRouteForDest(rreq.getDestinationId());
            route.getPrecursors().add(rreq.gatewayId);

            route.addPrecursor(rreq.gatewayId);

            // Update or create a route in routing table.
            aodvManager.addRoute(rreq.getSourceId(), rreq.getGatewayId() /* interface id*/, rreq.getHopCount(),
                    rreq.getSourceSequenceNumber(), Constants.NO_LIFE_TIME, route.getNextHop());

            RREP rrep = new RREP(rreq.getSourceId(), rreq.getDestinationId(), route.getDestinationSequenceNumber(),
                    rreq.getDestinationSequenceNumber(), route.getHopCount() + 1, Constants.LIFE_TIME);

            Log.i(TAG, "Send RREP=" + rrep);
            send(rrep, rreq.getSourceId());

            // Generate gratuitous RREP
            RREP gratuitousRREP = new RREP(rreq.getDestinationId(), rreq.getSourceId(), aodvManager.getOwnSequenceNum(),
                    route.getDestinationSequenceNumber(), rreq.getHopCount(), Constants.LIFE_TIME);

            send(gratuitousRREP, rreq.getDestinationId());

        } else {
            Log.i(TAG, "Broadcast RREQ=" + rreq);
            // Set the destination sequence number to the max of the one coming from originator and
            // one saved in this node.
            rreq.setDestinationSequenceNumber(
                    Math.max(
                            aodvManager.getDestSequenceNumber(rreq.getDestinationId()),
                            rreq.getDestinationSequenceNumber()));

            // Update or create a route in routing table.
            aodvManager.addRoute(rreq.getSourceId(), rreq.getGatewayId() /* interface id*/, rreq.getHopCount(),
                    rreq.getSourceSequenceNumber(), Constants.NO_LIFE_TIME, null);


            aodvManager.addBroadcast(rreq.getBroadcastId(), rreq.getSourceId());
            dataLinkManager.broadcast(rreq);
        }
    }

    private void processRREP(RREP rrep) {
        rrep.setHopCount(rrep.getHopCount() + 1);

        Log.d(TAG, "Received RREP = " + rrep);
        Log.i(TAG, "Routing table = " + aodvManager.printRoutingTable());


        if (rrep.getDestinationId().equals(myDevice.getId())) {
            // Save the destination sequence number into a hashmap
            aodvManager.saveDestSequenceNumber(rrep.getSourceId(), rrep.getSourceSequenceNumber());

            aodvManager.addRoute(rrep.getSourceId(), rrep.getGatewayId(), rrep.getHopCount(),
                    rrep.getSourceSequenceNumber(), rrep.getLifetime(), null);

            Log.i(TAG, "Routing table = " + aodvManager.printRoutingTable());

        } else if (aodvManager.containsDestination(rrep.getDestinationId())) {

            Route routeForRREPDestination = aodvManager.getRouteForDest(rrep.getDestinationId());

            aodvManager.addRoute(rrep.getSourceId(), rrep.getGatewayId(), rrep.getHopCount(),
                    rrep.getSourceSequenceNumber(), rrep.getLifetime(), routeForRREPDestination.getNextHop());

            aodvManager.printRoutingTable();

            dataLinkManager.sendDirect(rrep, routeForRREPDestination.getNextHop());
        } else {
            // TODO(Raise an ERROR)
            Log.i(TAG, "Could not find destination for RREP=" + rrep);
        }
    }

    private void processRERR(RERR rerr) {
        // TODO(Implement this)
        Log.i(TAG, "Received RERR, " + rerr);

        /*
        1. Get routes with rerr.getDestination as Destination and rerr.getGateway as nextHop
        2. Remove from Routing table routes with
        3. Send RERR to precursors
         */

        Route route = aodvManager.getAndRemoveRouteForDestAndNextHop(rerr.getUnreachableDestinationId(), rerr.getGatewayId());

        for (String pre : route.getPrecursors()) {
            RERR newRerr = new RERR(route.getDestinationId());
            dataLinkManager.sendDirect(newRerr, pre);
        }
    }

    public void joinNetwork() {
        dataLinkManager.joinNetwork();

        startHelloMessageTimer();
    }

    private void startHelloMessageTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "Start hello message task");
                broadcastHelloMessage();
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, HELLO_PROTOCOL_TIMER_INTERVAL);
    }

    private void broadcastHelloMessage() {
        long broadcastId = aodvManager.getNextBroadcastId();
        aodvManager.addBroadcast(broadcastId, myDevice.getId());

        HelloMessage helloMessage = new HelloMessage(myDevice, broadcastId);
        dataLinkManager.broadcast(helloMessage);
    }

    public void send(AdhocMessage message, String destinationId) {
        if (dataLinkManager.isDirectNeighbors(destinationId)) {
            Log.i(TAG, "Send to direct connect message=" + message);
            dataLinkManager.sendMessage(message, destinationId);
        } else if (aodvManager.containsDestination(destinationId)) {
            Log.i(TAG, "Send to known dest message=" + message);
            String nextHop = aodvManager.getNextHopId(destinationId);

            dataLinkManager.sendDirect(message, nextHop);
        } else {
            Log.i(TAG, "Don't know where, so broadcast RREQ =" + message);
            addToPendingMessages((DataMessage) message);
            proadcastRREQ(destinationId, aodvManager.getNextOwnSequenceNumber(), Constants.RREQ_RETRIES,
                    Constants.NET_TRANVERSAL_TIME);
        }
    }

    private void addToPendingMessages(DataMessage dataMessage) {
        if (pendingMessages.get(dataMessage.getDestinationId()) == null) {
            Queue<DataMessage> queue = new LinkedList<>();
            pendingMessages.put(dataMessage.getDestinationId(), queue);
        }

        pendingMessages.get(dataMessage.getDestinationId()).add(dataMessage);
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

    private void proadcastRREQ(String destinationId, long seqNumber, int retry, int time) {

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
                    proadcastRREQ(destinationId, seqNumber, retry - 1, time * 2);
                    Log.i(TAG, "Broadcast retry=" + retry + " , with " + rreqMessage);
                } else if (route != null) {
                    // TODO(Send the message)

                    while (pendingMessages.get(destinationId) != null &&
                            !pendingMessages.get(destinationId).isEmpty()) {
                        DataMessage dataMessage = pendingMessages.get(destinationId).poll();
                        dataLinkManager.sendDirect(dataMessage, route.getNextHop());
                    }
                } else {
                    // TODO(Raise an exception here to let the application know that the node not found)
//                    throw new DestinationUnreachableException();
                }
            }
        }, time);
    }
}
