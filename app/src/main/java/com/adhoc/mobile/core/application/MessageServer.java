package com.adhoc.mobile.core.application;

import com.adhoc.mobile.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class MessageServer extends Observable {

    private static final MessageServer instance = new MessageServer();
    private static final Map<String, List<Message>> messagesMap = new HashMap<>();
//    private static final Map<String, Observer> observersMap = new HashMap<>();

    private MessageServer() {
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
//        if (observersMap.containsKey(id)) {
//            setChanged();
//            notifyObservers();
//        }
    }

//    public void addObserver(Observer observer, String id) {
////        observersMap.put(id, observer);
//        addObserver(observer);
//    }

//    public void removeObserver(Observer observer) {
////        observersMap.remove(observer);
//        deleteObserver(observer);
//    }

    public static MessageServer getInstance() {
        return instance;
    }
}
