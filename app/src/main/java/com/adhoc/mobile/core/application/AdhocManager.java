package com.adhoc.mobile.core.application;

import android.content.Context;

import com.adhoc.mobile.core.network.NetworkCallbacks;
import com.adhoc.mobile.core.network.NetworkManager;
import com.adhoc.mobile.core.util.User;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AdhocManager {

    private final Context context;
    private final AdhocManagerCallbacks callbacks;
    private final NetworkManager networkManager;
    private final User user;
    private final Security security;

    private NetworkCallbacks networkCallbacks = new NetworkCallbacks() {

    };

    public AdhocManager(Context context, User user, AdhocManagerCallbacks callbacks) throws NoSuchAlgorithmException {
        this.context = context;
        this.callbacks = callbacks;
        this.user = user;

        security = new Security();
        networkManager = new NetworkManager(context, networkCallbacks);
    }

    public void joinNetwork() {
        networkManager.joinNetwork();
    }

    public void leaveNetwork() {
        networkManager.leaveNetwork();
    }

    public void sendMessage(String message, String destination) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        // 1.TODO we will need to extract public key from destination
        String encryptedMessage = security.encrypt(message, security.getPublicKey());
        networkManager.sendMessage(encryptedMessage);

    }
}
