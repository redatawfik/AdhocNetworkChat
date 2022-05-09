package com.adhoc.mobile.core.application;

import com.adhoc.mobile.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class MessageServer extends Observable {

    private static final MessageServer instance = new MessageServer();
    private static final Map<String, List<Message>> messagesMap = new HashMap<>();

    public static AdhocManager adhocManager;

    private MessageServer() {
    }

    public static MessageServer getInstance() {
        return instance;
    }

    public List<Message> getMessagesForId(String id) {
        messagesMap.computeIfAbsent(id, k -> new ArrayList<>());
        return messagesMap.get(id);
    }

    public void addMessageForId(String message, String id, String sourceType) {
        if (!messagesMap.containsKey(id)) {
            messagesMap.put(id, new ArrayList<>());
        }
        messagesMap.get(id).add(new Message(message, sourceType));

        setChanged();
        notifyObservers();
    }
}
