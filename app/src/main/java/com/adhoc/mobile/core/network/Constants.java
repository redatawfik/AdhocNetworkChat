package com.adhoc.mobile.core.network;

public class Constants {
    public static final long UNKNOWN_SEQUENCE_NUMBER = 0;
    public static final long MAX_VALID_SEQ_NUM = Long.MAX_VALUE;
    public static final long MIN_VALID_SEQ_NUM = Long.MIN_VALUE;
    public static final int RREQ_RETRIES = 3;
    public static final int NET_TRANVERSAL_TIME = 2800;
    public static final int NO_LIFE_TIME = -1;

    public static int EXPIRED_TABLE = 10000;
    public static int EXPIRED_TIME = EXPIRED_TABLE * 2;

    // Alive time for a route
    public static int LIFE_TIME = EXPIRED_TIME;

    public static int HELLO_PROTOCOL_TIMER_INTERVAL = 60000;

    public static int ACTIVE_ROUTE_TIMEOUT = 300000;
}
