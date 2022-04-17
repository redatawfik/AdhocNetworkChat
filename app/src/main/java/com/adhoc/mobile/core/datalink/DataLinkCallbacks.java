package com.adhoc.mobile.core.datalink;

public interface DataLinkCallbacks {

    void onConnectionSucceed(String id, String endpointInfo);

    void onDisconnected(String endpointId);

    void onPayloadReceived(String endpointId, String message);
}
