package com.adhoc.mobile.core.network;

import com.adhoc.mobile.core.datalink.AdhocDevice;

public interface NetworkCallbacks {

    void onConnectionSucceed(AdhocDevice device);

    void onDisconnected(String id);

    void onPayloadReceived(String sourceId, String destinationId, String message);

}
