package com.adhoc.mobile.core.network;

import java.util.HashMap;

public class AovdManager {

    private HashMap<String, Long> mapDestSequenceNumber;
    private long ownSequenceNum;
    private long broadCastId;
    private RoutingTable routingTable;

    AovdManager() {
        mapDestSequenceNumber = new HashMap<>();
    }

    public boolean knowNextHop(String destinationId) {
        return false;
    }

    public String getNextHop(String destinationId) {
        return null;
    }

    public long getDestSequenceNumber(String dest) {
        if (mapDestSequenceNumber.containsKey(dest)) {
            return mapDestSequenceNumber.get(dest);
        }
        return Constants.UNKNOWN_SEQUENCE_NUMBER;
    }

    public long getNextSequenceNumber() {
        if (ownSequenceNum < Constants.MAX_VALID_SEQ_NUM) {
            return ++ownSequenceNum;
        } else {
            ownSequenceNum = Constants.MIN_VALID_SEQ_NUM;
            return ownSequenceNum;
        }
    }

    public long getNextBroadcastId() {
        if (broadCastId == Long.MAX_VALUE) {
            broadCastId = 1;
        }
        return broadCastId++;
    }

    public Route getRouteForDest(String destinationId) {
        return null;
    }
}
