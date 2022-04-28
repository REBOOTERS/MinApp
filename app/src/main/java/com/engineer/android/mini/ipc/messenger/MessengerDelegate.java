package com.engineer.android.mini.ipc.messenger;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.engineer.android.mini.ipc.IPCConstants;

/**
 * Created on 2021/3/31.
 *
 * @author rookie
 */
public class MessengerDelegate {
    private static final String TAG = "MessengerDelegate";

    public static Messenger provideMessenger(boolean uiThread) {
        if (uiThread) {
            return new Messenger(new MessengerHandler(Looper.myLooper()));
        }
        HandlerThread handlerThread = new HandlerThread("test");
        handlerThread.start();
        return new Messenger(new MessengerHandler(handlerThread.getLooper()));
    }


    private static class MessengerHandler extends Handler {
        MessengerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IPCConstants.MESSAGE_FROM_CLIENT:
                    Bundle data = msg.getData();
                    String result = data.getString("msg");
                    Log.e(TAG, "handleMessage from client: thread   = " + Thread.currentThread().getName());
                    Log.e(TAG, "handleMessage from client: data   = " + data);
                    Log.e(TAG, "handleMessage from client: result = " + result);

                    Messenger client = msg.replyTo;
                    Message reply = Message.obtain(null, IPCConstants.MESSAGE_FROM_SERVER);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "server got message, and replied with happy");
                    reply.setData(bundle);
                    try {
                        Thread.sleep(1000);
                        client.send(reply);
                    } catch (RemoteException | InterruptedException e) {
                        Log.e(TAG, "send to client error " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case IPCConstants.MESSAGE_FROM_SERVER:
                    Bundle data1 = msg.getData();
                    String result1 = data1.getString("reply");

                    Log.e(TAG, "handleMessage from server: thread   = " + Thread.currentThread().getName());
                    Log.e(TAG, "handleMessage from server: data   = " + data1);
                    Log.e(TAG, "handleMessage from server: result = " + result1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
