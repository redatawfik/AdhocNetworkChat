package com.adhoc.mobile.core.datalink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class AdhocDevice {

    private String name;
    private String id;
    private String encryptionKey;
    private String phoneNumber;

    public AdhocDevice() {
    }

    public AdhocDevice(String name, String phoneNumber, String id, String encryptionKey) {
        this.name = name;
        this.id = id;
        this.encryptionKey = encryptionKey;
        this.phoneNumber = phoneNumber;
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

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ignored) {
            return null;
        }
    }
}
