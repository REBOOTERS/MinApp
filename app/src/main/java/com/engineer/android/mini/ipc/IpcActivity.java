package com.engineer.android.mini.ipc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.engineer.android.mini.R;
import com.engineer.android.mini.ipc.messenger.MessengerService;

public class IpcActivity extends AppCompatActivity {

    private static final String TAG = "IpcActivity";

    private Messenger mRepliedMessenger = MessengerDelegate.provideMessenger();


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");
            Messenger messenger = new Messenger(service);
            Message message = Message.obtain(null, IPCConstants.MESSAGE_FROM_CLIENT);
            Bundle bundle = new Bundle();
            bundle.putString("msg", "this is from client");
            message.setData(bundle);
            message.replyTo = mRepliedMessenger;
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc);
        findViewById(R.id.messenger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IpcActivity.this, MessengerService.class);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}