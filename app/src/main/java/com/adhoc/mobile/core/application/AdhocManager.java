package com.adhoc.mobile.core.application;

import android.content.Context;
import android.util.Log;

import com.adhoc.mobile.core.datalink.AdhocDevice;
import com.adhoc.mobile.core.network.NetworkCallbacks;
import com.adhoc.mobile.core.network.NetworkManager;

import java.util.UUID;

public class AdhocManager {

    private final String TAG = this.getClass().getName();

    private final AdhocManagerCallbacks callbacks;
    private final NetworkManager networkManager;
    private final Security security;

    private final NetworkCallbacks networkCallbacks = new NetworkCallbacks() {

        @Override
        public void onConnectionSucceed(AdhocDevice device) {
            callbacks.onConnectionSucceed(device);
        }

        @Override
        public void onDisconnected(String endpoint) {
            callbacks.onDisconnected(endpoint);
        }

        @Override
        public void onPayloadReceived(String endpointId, String message) {
            Log.i(TAG,"Received : " +  endpointId + message);
            callbacks.onPayloadReceived(endpointId, message);
        }
    };


    public AdhocManager(Context context, String name, AdhocManagerCallbacks callbacks) {
        this.callbacks = callbacks;
        this.security = new Security();

        AdhocDevice myDevice = createMyAdhocDevice(name);

        networkManager = new NetworkManager(context, myDevice, networkCallbacks);
    }

    private AdhocDevice createMyAdhocDevice(String name) {
        String uuid = getUUID();
        String encryptionKey = security.getPublicKey();

        return new AdhocDevice(name, uuid, encryptionKey);
    }

    public void joinNetwork() {
        networkManager.joinNetwork();
    }

    public void leaveNetwork() {
        networkManager.leaveNetwork();
    }

    public void sendMessage(String message, AdhocDevice destination) {

        // 1.TODO we will need to extract public key from destination

        String encryptedMessage = security.encrypt(message, destination.getEncryptionKey());

        networkManager.sendMessage(message, destination);
    }

    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
