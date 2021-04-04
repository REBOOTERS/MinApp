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
import com.engineer.android.mini.ipc.aidl.Book;
import com.engineer.android.mini.ipc.aidl.BookManagerService;
import com.engineer.android.mini.ipc.aidl.IBookInterface;
import com.engineer.android.mini.ipc.messenger.MessengerService;

import java.util.List;

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
        findViewById(R.id.aidl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBookManger();
            }
        });
    }

    private ServiceConnection mBookconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBookInterface iBookInterface = IBookInterface.Stub.asInterface(service);

            try {
                List<Book> books = iBookInterface.getBookList();
                Log.e(TAG, "onServiceConnected: list type is " + books.getClass().getCanonicalName());
                Log.e(TAG, "onServiceConnected: books = " + books.toString());


                Book book = new Book(102, "人类群星闪耀时");
                iBookInterface.addBook(book);
                Log.e(TAG, "onServiceConnected: after add " + (iBookInterface.getBookList().toString()));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void startBookManger() {
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mBookconn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        unbindService(mBookconn);
    }
}