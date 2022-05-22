package com.adhoc.mobile.core.network;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;


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

        Log.i(TAG, this.toString());
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
}
