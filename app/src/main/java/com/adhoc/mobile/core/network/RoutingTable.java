package com.adhoc.mobile.core.network;

import android.util.Log;

import java.util.HashMap;


public class RoutingTable {
    private final String TAG = this.getClass().getName();

    private final HashMap<String, Route> table;

    RoutingTable() {
        table = new HashMap<>();
    }


    void addRoute(Route newRoute) {
        Route existingRoute = table.get(newRoute.getDestinationId());

        if (existingRoute == null || existingRoute.getHopCount() >= newRoute.getHopCount()) {
            Log.i(TAG, "addRoute: Add or Update route=" + newRoute);
            table.put(newRoute.getDestinationId(), newRoute);
        }
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
}
