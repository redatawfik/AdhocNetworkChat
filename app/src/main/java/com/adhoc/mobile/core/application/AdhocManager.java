package com.adhoc.mobile.core.application;

import android.content.Context;

import com.adhoc.mobile.core.network.NetworkCallbacks;
import com.adhoc.mobile.core.network.NetworkManager;
import com.adhoc.mobile.core.util.User;

public class AdhocManager {

    private final Context context;
    private final AdhocManagerCallbacks callbacks;
    private final NetworkManager networkManager;
    private final User user;

    private NetworkCallbacks networkCallbacks = new NetworkCallbacks() {

    };

    public AdhocManager(Context context, User user, AdhocManagerCallbacks callbacks) {
        this.context = context;
        this.callbacks = callbacks;
        this.user = user;

        networkManager = new NetworkManager(context, networkCallbacks);
    }

    public void joinNetwork() {
        networkManager.joinNetwork();
    }

    public void leaveNetwork() {
        networkManager.leaveNetwork();
    }

    public void sendMessage(String message, String destination) {


        // 1.TODO(Encrypt the message)
        // 2.TODO(Call NetworkManager.sendMessage())
    }
}
