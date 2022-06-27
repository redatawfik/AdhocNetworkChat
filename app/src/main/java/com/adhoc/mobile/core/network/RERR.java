package com.adhoc.mobile.core.network;

import lombok.Data;

@Data
public class RERR extends AdhocMessage {
    private String unreachableDestinationId;

    public RERR() {
        super(MessageType.RERR);
    }

    public RERR(String destinationId) {
        super(MessageType.RERR);
        this.unreachableDestinationId = destinationId;
    }
}

