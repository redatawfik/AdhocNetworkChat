package com.adhoc.mobile.core.network;

import java.util.LinkedList;

import lombok.Data;

@Data
public class RERR extends AdhocMessage {
    private String unreachableDestinationId;
    private LinkedList<Long> unreachableDestinationSeqNum;

    public RERR() {
        super(MessageType.RERR);
    }

    public RERR(String unreachableDestinationId, LinkedList<Long> unreachableDestinationSeqNum) {
        this.unreachableDestinationId = unreachableDestinationId;
        this.unreachableDestinationSeqNum = unreachableDestinationSeqNum;
    }

    //adds unreachable to construct route sequence
    public void addToSeq(Long unreachableID){
        unreachableDestinationSeqNum.add(unreachableID);
    }
}

