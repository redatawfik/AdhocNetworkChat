package com.adhoc.mobile.core.network;

import com.adhoc.mobile.core.application.Endpoint;

public interface NetworkCallbacks {

    void onConnectionSucceed(Endpoint endpoint);

    void onDisconnected(String endpoint);

    void onPayloadReceived(String endpointId, String message);

}
