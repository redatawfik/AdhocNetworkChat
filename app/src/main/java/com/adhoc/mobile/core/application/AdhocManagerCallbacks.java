package com.adhoc.mobile.core.application;

import com.adhoc.mobile.core.datalink.AdhocDevice;

public interface AdhocManagerCallbacks {

    void onConnectionSucceed(AdhocDevice device);

    void onDisconnected(String endpointId);

    void notifyMessageReceivedFrom(String sourceId);
}
