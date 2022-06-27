package com.adhoc.mobile.core.network;

import static com.adhoc.mobile.core.network.Constants.ACTIVE_ROUTE_TIMEOUT;

import java.util.HashSet;
import java.util.Set;

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
    private Set<String> precursors;

    void updateTimeToLive() {
        this.timeToLive = System.currentTimeMillis() + ACTIVE_ROUTE_TIMEOUT;
    }

    public void addPrecursor(String id) {
        if (precursors == null) {
            precursors = new HashSet<>();
        }

        precursors.add(id);
    }
}
