package com.adhoc.mobile.core.network;

import androidx.annotation.NonNull;

public class RREP extends AdhocMessage {

    private String destinationId;
    private long sequenceNumber;
    private String originId;
    private int hopCount;
    private long lifetime;

    @NonNull
    @Override
    public String toString() {
        return "RREQ{" +
                "type=" + getType() +
                ", destinationId=" + destinationId +
                ", sequenceNumber=" + sequenceNumber +
                ", originId=" + originId +
                ", hopCount='" + hopCount + '\'' +
                ", lifetime=" + lifetime +
                '}';
    }
}
