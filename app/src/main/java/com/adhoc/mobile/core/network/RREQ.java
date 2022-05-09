package com.adhoc.mobile.core.network;


import lombok.Data;

@Data
public class RREQ extends AdhocMessage {

    private String sourceId;
    private long sourceSequenceNumber; // incremented
    private String destinationId;
    private long destinationSequenceNumber; // last destination sequence number for node 7
    private long broadcastId; // incremented
    private int hopCount; // number of hops to discover the destination

    public RREQ() {
        super(MessageType.RREQ);
    }

    public RREQ(String sourceId, String destinationId, long sourceSequenceNumber,
                long destinationSequenceNumber, long broadcastId, int hopCount) {
        super(MessageType.RREQ);
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.sourceSequenceNumber = sourceSequenceNumber;
        this.destinationSequenceNumber = destinationSequenceNumber;
        this.broadcastId = broadcastId;
        this.hopCount = hopCount;
    }

    public void incrementHopCount() {
        this.hopCount++;
    }
}