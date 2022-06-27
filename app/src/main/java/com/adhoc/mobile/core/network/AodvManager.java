package com.adhoc.mobile.core.network;

import static com.adhoc.mobile.core.network.Constants.ACTIVE_ROUTE_TIMEOUT;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AodvManager {

    private long ownSequenceNum = 0;
    private long broadCastId = 1;
    private RoutingTable routingTable = new RoutingTable();
    private HashSet<String> broadcastsSet = new HashSet<>();
    private HashMap<String, Long> mapDestSequenceNumber = new HashMap<>();

    public void addRoute(String destinationId, String next, int hopCount,
                         long destinationSequenceNumber, long lifeTime, String precursor) {

        if (lifeTime == 0) lifeTime = ACTIVE_ROUTE_TIMEOUT;

        Route route = new Route(destinationId, next, hopCount, destinationSequenceNumber, lifeTime,
                Collections.singleton(precursor));
        routingTable.addRoute(route);
    }

    public void addBroadcast(long broadcastId, String sourceId) {
        String value = sourceId + broadcastId;
        broadcastsSet.add(value);
    }

    public Route getRouteForDest(String destinationId) {
        return routingTable.getRoute(destinationId);
    }

    public boolean containsDestination(String destinationId) {
        return routingTable.isContainDestination(destinationId);
    }

    public String getNextHopId(String destinationId) {
        return getRouteForDest(destinationId).getNextHop();
    }

    public void removeRouteForDestination(String destinationId) {
        routingTable.removeRoute(destinationId);
    }

    public void saveDestSequenceNumber(String id, long sequenceNumber) {
        mapDestSequenceNumber.put(id, sequenceNumber);
    }

    public Long getDestSequenceNumber(String dest) {
        if (mapDestSequenceNumber.containsKey(dest)) {
            return mapDestSequenceNumber.get(dest);
        }
        return Constants.UNKNOWN_SEQUENCE_NUMBER;
    }

    public long getNextOwnSequenceNumber() {
        if (ownSequenceNum < Constants.MAX_VALID_SEQ_NUM) {
            return ++ownSequenceNum;
        } else {
            ownSequenceNum = Constants.MIN_VALID_SEQ_NUM;
            return ownSequenceNum;
        }
    }

    public long getOwnSequenceNum() {
        return ownSequenceNum;
    }

    public long getNextBroadcastId() {
        if (broadCastId == Long.MAX_VALUE) {
            broadCastId = 1;
        }
        return broadCastId++;
    }

    public boolean isReceivedBefore(long broadcastId, String sourceId) {
        String value = sourceId + broadcastId;
        return broadcastsSet.contains(value);
    }

    public String printRoutingTable() {
        return routingTable.toString();
    }

    public List<Route> getAndRemoveRoutesWithNextHop(String privateId) {
        return routingTable.getAndRemoveRoutesWithNextHop(privateId);
    }

    public Route getAndRemoveRouteForDestAndNextHop(String unreachableDestinationId, String gatewayId) {
        return routingTable.getAndRemoveRouteForDestAndNextHop(unreachableDestinationId, gatewayId);
    }
}
