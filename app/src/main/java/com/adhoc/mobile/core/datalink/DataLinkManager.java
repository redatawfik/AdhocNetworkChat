package com.adhoc.mobile.core.datalink;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;

public class DataLinkManager {

    private final String TAG = this.getClass().getName();
    private static final String NETWORK_NAME = "com.adhoc.mobile.core.datalink";

    private final Strategy STRATEGY = Strategy.P2P_CLUSTER;
    private final ConnectionsClient connectionsClient;
    private final Context context;
    private final String myName;
    private final DataLinkCallbacks callbacks;

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            if (payload.getType() == Payload.Type.BYTES) {
                byte[] receivedBytes = payload.asBytes();
                String message = new String(receivedBytes, StandardCharsets.UTF_8);

                Log.i(TAG, "Received : " + message);

                // TODO(Inform network layer)
                callbacks.onPayloadReceived(endpointId, message);
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };
    // Callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Log.i(TAG, "Found endpoint with id = " + endpointId);
            connectionsClient.requestConnection(myName, endpointId, connectionLifecycleCallback);
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
            Log.i(TAG, "Lost endpoint with id = " + endpointId);
        }
    };
    String tempEndpointName = "";
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback);
            tempEndpointName = connectionInfo.getEndpointName();
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
            Log.i(TAG, "connection status is " + result.getStatus());
            if (result.getStatus().isSuccess()) {
                callbacks.onConnectionSucceed(endpointId, tempEndpointName);
            }
        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            callbacks.onDisconnected(endpointId);
        }
    };


    public DataLinkManager(Context context, String myName, DataLinkCallbacks callbacks) {
        this.myName = myName;
        this.context = context;
        this.callbacks = callbacks;
        this.connectionsClient = Nearby.getConnectionsClient(context);
    }

    //---------------------------------------Public methods----------------------------------------

    public void joinNetwork() {
        startAdvertising();
        startDiscovery();
    }

    public void sendMessage(String message, String id) {
        connectionsClient.sendPayload(
                id,
                Payload.fromBytes(message.getBytes())
        );
    }

    public void leaveNetwork() {
        stopAdvertising();
        stopDiscovery();

        connectionsClient.stopAllEndpoints();
    }


    //---------------------------------------Private methods----------------------------------------

    private void startAdvertising() {
        Log.i(TAG, "Started advertising on " + NETWORK_NAME);

        AdvertisingOptions options = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startAdvertising(
                myName,
                NETWORK_NAME,
                connectionLifecycleCallback,
                options
        );
    }

    private void startDiscovery() {
        Log.i(TAG, "Started discovery on " + NETWORK_NAME);

        DiscoveryOptions options = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startDiscovery(
                NETWORK_NAME,
                endpointDiscoveryCallback,
                options
        );
    }

    private void stopAdvertising() {
        connectionsClient.stopAdvertising();
    }

    private void stopDiscovery() {
        connectionsClient.stopDiscovery();
    }
}
