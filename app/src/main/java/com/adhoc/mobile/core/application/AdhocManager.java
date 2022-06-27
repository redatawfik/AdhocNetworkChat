package com.adhoc.mobile.core.application;

import android.content.Context;
import android.util.Log;

import com.adhoc.mobile.Message;
import com.adhoc.mobile.core.datalink.AdhocDevice;
import com.adhoc.mobile.core.network.NetworkCallbacks;
import com.adhoc.mobile.core.network.NetworkManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdhocManager {

    private static AdhocManager instance;
    private final String TAG = this.getClass().getName();
    private final AdhocManagerCallbacks callbacks;
    private final NetworkManager networkManager;
    private final Security security;
    private final MessageServer messageServer;
    private final Map<String, AdhocDevice> adhocDeviceMap;

    private final NetworkCallbacks networkCallbacks = new NetworkCallbacks() {

        @Override
        public void onConnectionSucceed(AdhocDevice device) {
            if (!adhocDeviceMap.containsKey(device.getId())) {
                adhocDeviceMap.put(device.getId(), device);
                callbacks.onConnectionSucceed(device);
            }
        }

        @Override
        public void onDisconnected(String endpoint) {
            callbacks.onDisconnected(endpoint);
        }

        @Override
        public void onPayloadReceived(String sourceId, String destinationId, String message) {
            Log.i(TAG, "Received : " + sourceId + message);

            messageServer.addMessageForId(message, sourceId, Message.MESSAGE_RECEIVED_TYPE);
        }
    };

    private AdhocManager(Context context, String name, String phoneNumber, AdhocManagerCallbacks callbacks) {
        this.callbacks = callbacks;
        this.security = new Security();
        this.messageServer = MessageServer.getInstance();
        this.adhocDeviceMap = new HashMap<>();

        AdhocDevice myDevice = createMyAdhocDevice(name, phoneNumber);

        networkManager = new NetworkManager(context, myDevice, networkCallbacks);
        Log.i(TAG, "A new instance of AdhocManager is created");
    }

    public static AdhocManager getInstance(Context context, String name, String phoneNumber, AdhocManagerCallbacks callbacks) {
        if (instance == null) {
            instance = new AdhocManager(context, name, phoneNumber, callbacks);
        }
        return instance;
    }

    public static AdhocManager getInstance() {
        assert instance != null;
        return instance;
    }

    private AdhocDevice createMyAdhocDevice(String name, String phoneNumber) {
        String uuid = getUUID();
        String encryptionKey = security.getPublicKey();

        return new AdhocDevice(name, phoneNumber, uuid, encryptionKey);
    }

    public void joinNetwork() {
        networkManager.joinNetwork();
    }

    public void leaveNetwork() {
        networkManager.leaveNetwork();
        instance = null;
    }

    public void sendMessage(String message, String destinationId) {

        AdhocDevice adhocDevice = adhocDeviceMap.get(destinationId);
        // 1.TODO we will need to extract public key from destination

//        String encryptedMessage = security.encrypt(message, adhocDevice.getEncryptionKey());

        networkManager.sendMessage(message, adhocDevice);
    }

    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
