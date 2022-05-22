package com.adhoc.mobile.core.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    public static AdhocMessage getObjectFromJson(String json, String endpointId) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            AdhocMessage adhocMessage = mapper.readValue(json, AdhocMessage.class);

            switch (adhocMessage.getType()) {
                case DATA:
                    adhocMessage = mapper.readValue(json, DataMessage.class);
                    break;
                case RREQ:
                    adhocMessage = mapper.readValue(json, RREQ.class);
                    break;
                case RREP:
                    adhocMessage = mapper.readValue(json, RREP.class);
                    break;
                case RERR:
                    adhocMessage = mapper.readValue(json, RERR.class);
                    break;
                case HELLO:
                    adhocMessage = mapper.readValue(json, HelloMessage.class);
                    break;
            }

            adhocMessage.setGatewayId(endpointId);
            return adhocMessage;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
