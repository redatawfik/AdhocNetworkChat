package com.adhoc.mobile.core.datalink;

public interface DataLinkCallbacks {

    void onConnectionSucceed(AdhocDevice adhocDevice);

    void onDisconnected(String publicId, String privateId);

    void onPayloadReceived(String message, String endpointId);
}
