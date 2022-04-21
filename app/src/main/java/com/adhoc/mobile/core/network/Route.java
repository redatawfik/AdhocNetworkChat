package com.adhoc.mobile.core.network;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class Route {
    private String destinationId;
    private String nextHop;
    private int hopCount;
    private long destinationSequenceNumber;
    private long timeToLive;
    private ArrayList<String> precursors;
    private ConcurrentHashMap<String, Long> activesDataPath;
}
