package com.adhoc.mobile.core.network;

import java.util.HashMap;

public class RoutingTable {
    private final HashMap<String, Route> routingTable;

    RoutingTable() {
        routingTable = new HashMap<>();
    }
}
