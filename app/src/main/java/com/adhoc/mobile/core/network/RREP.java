package com.adhoc.mobile.core.network;

import lombok.Data;

@Data
public class RREP extends AdhocMessage {

    // TODO(Might need to add destination sequence number)
    private String sourceId;
    private long sourceSequenceNumber;
    private String destinationId;
    private long destinationSequenceNumber;
    private int hopCount;
    private long lifetime;

    public RREP() {
        super(MessageType.RREP);
    }

    public RREP(String destinationId, String sourceId, long sourceSequenceNumber, long destinationSequenceNumber,
                int hopCount, long lifetime) {
        super(MessageType.RREP);
        this.destinationId = destinationId;
        this.sourceId = sourceId;
        this.destinationSequenceNumber = destinationSequenceNumber;
        this.sourceSequenceNumber = sourceSequenceNumber;
        this.hopCount = hopCount;
        this.lifetime = lifetime;
    }
}
