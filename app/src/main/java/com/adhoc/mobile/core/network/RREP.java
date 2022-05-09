package com.adhoc.mobile.core.network;

import lombok.Data;

@Data
public class RREP extends AdhocMessage {

    private String sourceId;
    private long sourceSequenceNumber;
    private String destinationId;
    private int hopCount;
    private long lifetime;

    public RREP() {
        super(MessageType.RREP);
    }

    public RREP(String destinationId, String sourceId, long sourceSequenceNumber, int hopCount, long lifetime) {
        super(MessageType.RREP);
        this.destinationId = destinationId;
        this.sourceId = sourceId;
        this.sourceSequenceNumber = sourceSequenceNumber;
        this.hopCount = hopCount;
        this.lifetime = lifetime;
    }
}
