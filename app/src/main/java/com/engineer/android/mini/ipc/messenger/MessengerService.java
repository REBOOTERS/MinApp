package com.engineer.android.mini.ipc.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.NonNull;

import com.engineer.android.mini.ipc.IPCConstants;
import com.engineer.android.mini.ipc.MessengerDelegate;

public class MessengerService extends Service {
    private static final String TAG = "MessengerService";

    public MessengerService() {
    }

    private final Messenger messenger = MessengerDelegate.provideMessenger();

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}