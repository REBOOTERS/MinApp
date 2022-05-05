package com.engineer.android.mini.ipc.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";

    public BookManagerService() {
    }

    private ServiceBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate() called on thread: " + Thread.currentThread().getName());
        Log.e(TAG, "onCreate() called on " + Process.myPid() + "-" + Thread.currentThread().getId());
        mBinder = new ServiceBinder();
        mBinder.provideBookList().add(new Book(100, "Android"));
        mBinder.provideBookList().add(new Book(101, "iOS"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind() called with: intent = [" + intent + "] on thread "
                + Thread.currentThread().getName());
        Log.e("TGA_TAG", "mBinder is " + mBinder);
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags
                + "], startId = [" + startId + "]");
        Log.d(TAG, "onStartCommand() called with: on thread "
                + Thread.currentThread().getName());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind() called with: intent = [" + intent + "]");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
    }
}