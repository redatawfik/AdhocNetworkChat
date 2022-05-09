package com.adhoc.mobile.core.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class AdhocMessage {

    public String gatewayId;
    private MessageType type;

    public AdhocMessage() {
    }

    public AdhocMessage(MessageType type) {
        this.type = type;
    }

    public String toJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ignored) {
            return null;
        }
    }
}
