package com.adhoc.mobile.core.application;

import android.content.Context;

import com.adhoc.mobile.core.network.NetworkCallbacks;
import com.adhoc.mobile.core.network.NetworkManager;

public class AdhocManager {

    private final String TAG = this.getClass().getName();

    private final Context context;
    private final AdhocManagerCallbacks callbacks;
    private final NetworkManager networkManager;
    private final String myName;

    private final NetworkCallbacks networkCallbacks = new NetworkCallbacks() {

        @Override
        public void onConnectionSucceed(Endpoint endpoint) {
            callbacks.onConnectionSucceed(endpoint);
        }

        @Override
        public void onDisconnected(String endpoint) {
            callbacks.onDisconnected(endpoint);
        }

        @Override
        public void onPayloadReceived(String endpointId, String message) {
            callbacks.onPayloadReceived(endpointId, message);
        }
    };


    public AdhocManager(Context context, String myName, AdhocManagerCallbacks callbacks) {
        this.context = context;
        this.callbacks = callbacks;
        this.myName = myName;

//        security = new Security();
        networkManager = new NetworkManager(context, myName, networkCallbacks);
    }

    public void joinNetwork() {
        networkManager.joinNetwork();
    }

    public void leaveNetwork() {
        networkManager.leaveNetwork();
    }

    public void sendMessage(String message, String destination) {

        // 1.TODO we will need to extract public key from destination
//        String encryptedMessage = security.encrypt(message, security.getPublicKey());
        networkManager.sendMessage(message, destination);

    }
}
