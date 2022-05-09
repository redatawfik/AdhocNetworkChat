package com.adhoc.mobile.core.network;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Route {

    private String destinationId;
    private String nextHop;
    private int hopCount;
    private long destinationSequenceNumber;
    private long timeToLive;
    private List<String> precursors;
}
