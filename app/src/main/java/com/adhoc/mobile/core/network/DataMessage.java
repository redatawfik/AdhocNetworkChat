package com.adhoc.mobile.core.network;

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

    public Object getPayload() {
        return payload;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }
}