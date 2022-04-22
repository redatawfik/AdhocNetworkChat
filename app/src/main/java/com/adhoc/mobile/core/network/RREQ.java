package com.adhoc.mobile.core.network;


import androidx.annotation.NonNull;

public class RREQ extends AdhocMessage {

    private String sourceId;
    private String destinationId;
    private long sequenceNumber; // incremented
    private long destinationSequenceNumber; // last destination sequence number for node 7
    private long broadcastId; // incremented
    private int hopCount; // number of hops to discover the destination

    public RREQ(String sourceId, String destinationId, long sequenceNumber,
                long destinationSequenceNumber, long broadcastId, int hopCount) {
        super(MessageType.RREQ);
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.sequenceNumber = sequenceNumber;
        this.destinationSequenceNumber = destinationSequenceNumber;
        this.broadcastId = broadcastId;
        this.hopCount = hopCount;
    }

    @NonNull
    @Override
    public String toString() {
        return "RREQ{" +
                "type=" + getType() +
                ", sourceId=" + sourceId +
                ", destinationId=" + destinationId +
                ", sequenceNumber=" + sequenceNumber +
                ", destinationSequenceNumber='" + destinationSequenceNumber + '\'' +
                ", hopCount=" + hopCount +
                '}';
    }
}