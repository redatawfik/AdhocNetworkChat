package com.adhoc.mobile.core.network;

import com.adhoc.mobile.core.datalink.AdhocDevice;

import lombok.Data;

@Data
public class HelloMessage extends AdhocMessage {
    private AdhocDevice adhocDevice;
    private long broadcastId;

    public HelloMessage() {
        super(MessageType.HELLO);
    }

    public HelloMessage(AdhocDevice adhocDevice, long broadcastId) {
        super(MessageType.HELLO);
        this.adhocDevice = adhocDevice;
        this.broadcastId = broadcastId;
    }
}
