package com.engineer.android.mini.ipc.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;

public class MessengerService extends Service {

    public MessengerService() {
    }

    private final Messenger messenger = MessengerDelegate.provideMessenger(false);

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}