package com.adhoc.mobile.core.application;

public class Endpoint {

    private String id;
    private String name;

    public Endpoint(String endpointId, String endpointName) {
        this.id = endpointId;
        this.name = endpointName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
