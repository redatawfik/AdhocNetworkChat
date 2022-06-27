package com.adhoc.mobile.core.network;

import static com.adhoc.mobile.core.network.Constants.ACTIVE_ROUTE_TIMEOUT;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoutingTable {
    private final String TAG = this.getClass().getName();

    private final HashMap<String, Route> table;

    RoutingTable() {
        table = new HashMap<>();
    }


    void addRoute(Route newRoute) {
        Route existingRoute = table.get(newRoute.getDestinationId());

        if (existingRoute == null) {
            table.put(newRoute.getDestinationId(), newRoute);
        } else if (existingRoute.getDestinationSequenceNumber() < newRoute.getDestinationSequenceNumber()
                || (existingRoute.getDestinationSequenceNumber() == newRoute.getDestinationSequenceNumber()
                && newRoute.getHopCount() + 1 < existingRoute.getHopCount())) {

            Log.i(TAG, "addRoute: Add or Update route=" + newRoute);
            newRoute.setTimeToLive(System.currentTimeMillis() + newRoute.getTimeToLive());

            newRoute.getPrecursors().addAll(existingRoute.getPrecursors());
            table.put(newRoute.getDestinationId(), newRoute);
        } else {
            existingRoute.getPrecursors().addAll(newRoute.getPrecursors());
        }

        Log.i(TAG, this.toString());
    }

    void updateTimeToLive(String id) {
        Route route = table.get(id);
        if (route == null) return;

        route.setTimeToLive(System.currentTimeMillis() + ACTIVE_ROUTE_TIMEOUT);
    }

    void removeRoute(String destinationId) {
        table.remove(destinationId);
    }

    Route getRoute(String destinationId) {
        return table.get(destinationId);
    }

    boolean isContainDestination(String destinationId) {
        return table.containsKey(destinationId);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Destination Id | destinationId | nextHop | hopCount | destinationSequenceNumber | timeToLive | precursors| \n");

        for (Map.Entry<String, Route> entry : table.entrySet()) {
            sb.append(entry.getValue().toString()).append("\n");
        }

        return sb.toString();
    }

    public List<Route> getAndRemoveRoutesWithNextHop(String privateId) {
        List<Route> routeList = new ArrayList<>();
        for (Map.Entry<String, Route> entry : table.entrySet()) {
            if (entry.getValue().getNextHop().equals(privateId)) {
                routeList.add(entry.getValue());
                table.remove(entry.getKey());
            }
        }

        return routeList;
    }

    public Route getAndRemoveRouteForDestAndNextHop(String unreachableDestinationId, String gatewayId) {
        for (Map.Entry<String, Route> entry : table.entrySet()) {
            if (entry.getValue().getDestinationId().equals(unreachableDestinationId) &&
                    entry.getValue().getNextHop().equals(gatewayId)) {
                table.remove(entry.getKey());
                return entry.getValue();

            }
        }
        return null;
    }
}
