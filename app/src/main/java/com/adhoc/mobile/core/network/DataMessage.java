package com.adhoc.mobile.core.network;

public class DataMessage extends AdhocMessage {

    private String destinationId;
    private String payload;


    public DataMessage() {
        super(MessageType.DATA);
        destinationId = "";
        payload = null;
    }

    public DataMessage(String destinationId, String payload) {
        super(MessageType.DATA);
        this.destinationId = destinationId;
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}