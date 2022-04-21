package com.adhoc.mobile.core.network;


public class RREQ extends AdhocMessage {

    private String sourceId;
    private String destinationId;
    private String sequenceNumber; // incremented
    private String destinationSequenceNumber; // last destination sequence number for node 7
    private String broadcastId; // incremented
    private int hopCount; // number of hops to discover the destination
    private long lifetime;
}