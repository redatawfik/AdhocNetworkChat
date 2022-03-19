package com.adhoc.mobile.core.network;

import android.content.Context;

import com.adhoc.mobile.core.datalink.DataLinkCallbacks;
import com.adhoc.mobile.core.datalink.DataLinkManager;

import java.util.UUID;

public class NetworkManager {

    private final DataLinkManager dataLinkManager;
    private final String myId;
    private final Context context;
    private final NetworkCallbacks callbacks;

    private final DataLinkCallbacks dataLinkCallbacks = new DataLinkCallbacks() {
        @Override
        public void onConnectionSucceed(String endpointId) {

        }

        @Override
        public void onDisconnected(String endpointId) {

        }

        @Override
        public void onPayloadReceived(String endpointId, String message) {

        }
    };

    public NetworkManager(Context context, NetworkCallbacks callbacks) {
        this.context = context;
        this.callbacks = callbacks;
        this.myId = getUUID();
        dataLinkManager = new DataLinkManager(context, myId, dataLinkCallbacks);
    }

    public void joinNetwork() {
        dataLinkManager.joinNetwork();
    }

    public void sendMessage(String message) {
        // TODO(Look in Routing table)
//        dataLinkManager.sendMessage(message,);
    }

    public void leaveNetwork() {
        // TODO
    }

    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
