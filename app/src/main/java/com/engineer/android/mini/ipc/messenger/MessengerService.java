package com.engineer.android.mini.ipc.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;

import com.engineer.android.mini.ipc.MessengerDelegate;

public class MessengerService extends Service {

    public MessengerService() {
    }

    private final Messenger messenger = MessengerDelegate.provideMessenger();

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}