package com.adhoc.mobile.core.network;

import android.content.Context;

import com.adhoc.mobile.core.application.Endpoint;
import com.adhoc.mobile.core.datalink.DataLinkCallbacks;
import com.adhoc.mobile.core.datalink.DataLinkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NetworkManager {

    private final String TAG = this.getClass().getName();

    private final DataLinkManager dataLinkManager;
    private final String myId;
    private final Context context;
    private final NetworkCallbacks callbacks;
    private final String myName;


    private List<Endpoint> endpointList = new ArrayList<>();

    private final DataLinkCallbacks dataLinkCallbacks = new DataLinkCallbacks() {
        @Override
        public void onConnectionSucceed(String endpointId, String endpointName) {
            Endpoint endpoint = new Endpoint(endpointId, endpointName);
            endpointList.add(endpoint);
            callbacks.onConnectionSucceed(endpoint);
        }

        @Override
        public void onDisconnected(String endpointId) {
            endpointList = endpointList.stream()
                    .filter(e -> !e.getId().equals(endpointId)).collect(Collectors.toList());
            callbacks.onDisconnected(endpointId);
        }

        @Override
        public void onPayloadReceived(String endpointId, String message) {

        }
    };

    public NetworkManager(Context context, String myName, NetworkCallbacks callbacks) {
        this.context = context;
        this.callbacks = callbacks;
        this.myId = getUUID();
        this.myName = myName;
        dataLinkManager = new DataLinkManager(context, myName, dataLinkCallbacks);
    }

    public void joinNetwork() {
        dataLinkManager.joinNetwork();
    }

    public void sendMessage(String message, String destinationId) {
        // TODO(Look in Routing table)
        dataLinkManager.sendMessage(message, destinationId);
    }

    public void leaveNetwork() {
        // TODO
    }

    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
