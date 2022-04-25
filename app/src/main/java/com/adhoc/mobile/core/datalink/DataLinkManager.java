package com.adhoc.mobile.core.datalink;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

public class DataLinkManager {

    public static final String NETWORK_NAME = "com.adhoc.mobile.core.datalink";
    private final String TAG = this.getClass().getName();
    private final Strategy STRATEGY = Strategy.P2P_CLUSTER;
    private final ConnectionsClient connectionsClient;
    private final Context context;
    private final AdhocDevice myDevice;
    private final DataLinkCallbacks callbacks;
    private final Map<String, String> neighborDevicesMap = new HashMap<>();

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
//            Toast.makeText(context, "onPayloadReceived", Toast.LENGTH_SHORT).show();

            if (payload.getType() == Payload.Type.BYTES) {
                byte[] receivedBytes = payload.asBytes();
                String message = new String(receivedBytes, StandardCharsets.UTF_8);

                Log.i(TAG, "Received payload : " + message);

                // TODO(Inform network layer)
                callbacks.onPayloadReceived(message);
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
            Toast.makeText(context, "onPayloadTransferUpdate", Toast.LENGTH_SHORT).show();
        }
    };

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        AdhocDevice adhocDevice = null;

        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
//            Toast.makeText(context, "onConnectionInitiated", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Connection initiated to device=" + connectionInfo.getEndpointName());

            connectionsClient.acceptConnection(endpointId, payloadCallback);
            adhocDevice = AdhocDevice.fromJson(connectionInfo.getEndpointName());
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
//            Toast.makeText(context, "onConnectionResult", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Connection to device with id=" + endpointId + " is " + result.getStatus());

            if (result.getStatus().isSuccess()) {
                neighborDevicesMap.put(adhocDevice.getId(), endpointId);
                callbacks.onConnectionSucceed(adhocDevice);
            }
        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
//            Toast.makeText(context, "onDisconnected", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Disconnect from device with id=" + endpointId);
            String id = null;
            for (Map.Entry<String, String> entry : neighborDevicesMap.entrySet()) {
                if (entry.getValue().equals(endpointId)) {
                    id = entry.getKey();
                    neighborDevicesMap.remove(id);
                    break;
                }
            }

            if (id != null) {
                callbacks.onDisconnected(id);
            }
        }
    };

    // Callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
//            Toast.makeText(context, "onEndpointFound", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Found endpoint with id = " + endpointId);
            connectionsClient.requestConnection(myDevice.toJson(), endpointId, connectionLifecycleCallback);
            Log.i(TAG, "Requested connection with mydevice=" + myDevice.toJson() +
                    "to connect to device with id=" + endpointId);
        }

        @Override
        public void onEndpointLost(@NonNull String endpointId) {
//            Toast.makeText(context, "onEndpointLost", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Lost endpoint with id = " + endpointId);
        }
    };


    public DataLinkManager(Context context, AdhocDevice device, DataLinkCallbacks callbacks) {
        this.myDevice = device;
        this.context = context;
        this.callbacks = callbacks;
        this.connectionsClient = Nearby.getConnectionsClient(context);
    }

    //---------------------------------------Public methods----------------------------------------

    public void joinNetwork() {
        startAdvertising();
        startDiscovery();
    }

    public void sendMessage(String message, String address) {
        Log.i(TAG, "Send message=" + message + "to address=" + address);

        String privateId = neighborDevicesMap.get(address);

        assert privateId != null;
        connectionsClient.sendPayload(
                privateId,
                Payload.fromBytes(message.getBytes())
        );
    }

    public boolean isDirectNeighbors(String address) {
        return neighborDevicesMap.containsKey(address);
    }

    public void broadcast(String message) {
        Log.i(TAG, "Broadcast message=" + message + "to all neighbors");

        for (String id : neighborDevicesMap.values()) {
            sendMessage(message, id);
        }
    }

    public void broadcastExcept(String message, String excludedAddress) {
        Log.i(TAG, "Broadcast message=" + message + "to all neighbors except " + excludedAddress);

        for (String id : neighborDevicesMap.values()) {
            if (id.equals(excludedAddress)) continue;
            sendMessage(message, id);
        }
    }

    public void leaveNetwork() {
        Log.i(TAG, "Leave network");
        stopAdvertising();
        stopDiscovery();

        connectionsClient.stopAllEndpoints();
    }


    //---------------------------------------Private methods----------------------------------------

    private void startAdvertising() {
        Log.i(TAG, "Started advertising on " + NETWORK_NAME);

        AdvertisingOptions options = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        connectionsClient.startAdvertising(
                myDevice.toJson(),
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
