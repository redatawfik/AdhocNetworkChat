package com.adhoc.mobile.core.datalink;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AdhocDevice {

    private String name;

    private String id;

    private String encryptionKey;

    public AdhocDevice() {
    }

    public AdhocDevice(String name, String id, String encryptionKey) {
        this.name = name;
        this.id = id;
        this.encryptionKey = encryptionKey;
    }

    public static AdhocDevice fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, AdhocDevice.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ignored) {
            return null;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "AdHocDevice{" +
                "name=" + name + '\'' +
                ", id=" + id + '\'' +
                ", encryptionkey=" + encryptionKey +
                '}';
    }
}
