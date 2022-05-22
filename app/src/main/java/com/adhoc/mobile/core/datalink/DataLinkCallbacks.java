package com.adhoc.mobile.core.datalink;

public interface DataLinkCallbacks {

    void onConnectionSucceed(AdhocDevice adhocDevice);

    void onDisconnected(String endpointId);

    void onPayloadReceived(String message, String endpointId);
}
