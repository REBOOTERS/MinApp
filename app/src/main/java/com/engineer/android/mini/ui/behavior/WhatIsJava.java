package com.engineer.android.mini.ui.behavior;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * Created on 2021/5/8.
 *
 * @author rookie
 */
class WhatIsJava {
    public static void main(String[] args) {
        Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                System.out.println(msg.arg1);
                System.out.println(msg.obj);
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = 100;
                message.obj = new WhatIsJava();
                handler.sendMessage(message);
            }
        }).start();
    }
}
