package com.adhoc.mobile.core.application;

public interface AdhocManagerCallbacks {

    void onConnectionSucceed(Endpoint endpoint);

    void onDisconnected(String endpointId);

    void onPayloadReceived(String endpointId, String message);

}
