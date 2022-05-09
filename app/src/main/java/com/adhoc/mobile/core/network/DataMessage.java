package com.adhoc.mobile.core.network;

import lombok.Data;

@Data
public class DataMessage extends AdhocMessage {

    private String destinationId;
    private String originId;
    private String payload;


    public DataMessage() {
        super(MessageType.DATA);
        destinationId = "";
        payload = null;
    }

    public DataMessage(String destinationId, String originId, String payload) {
        super(MessageType.DATA);
        this.destinationId = destinationId;
        this.originId = originId;
        this.payload = payload;
    }
}