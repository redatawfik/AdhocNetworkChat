package com.adhoc.mobile.core.network;

import lombok.Data;

@Data
public class RERR extends AdhocMessage {
    private String unreachableDestinationId;
    private long unreachableDestinationSeqNum;
}

