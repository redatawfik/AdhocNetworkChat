package com.adhoc.mobile;

public class Message {

    public final static String MESSAGE_SENT_TYPE = "SENT";
    public final static String MESSAGE_RECEIVED_TYPE = "RECEIVED";

    private String messageContent;
    private String messageType;

    public Message(String messageContent, String messageType) {
        this.messageContent = messageContent;
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent;
    }


    public String getMessageType() {
        return messageType;
    }

}
