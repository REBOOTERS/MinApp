package com.engineer.android.mini.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.engineer.android.mini.R;
import com.engineer.android.mini.ipc.aidl.Book;
import com.engineer.android.mini.ipc.aidl.BookManagerService;
import com.engineer.android.mini.ipc.aidl.IBookInfoCallback;
import com.engineer.android.mini.ipc.aidl.IBookInterface;
import com.engineer.android.mini.ipc.messenger.MessengerService;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class IpcActivity extends AppCompatActivity {

    private static final String TAG = "IpcActivity";

    private boolean isConnectionRegistered = false;
    private boolean isBookServiceRegistered = false;

    private final Messenger mRepliedMessenger = MessengerDelegate.provideMessenger();

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected() called with: "
                    + "name = [" + name + "]," + " service = [" + service + "]");
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


    private IBookInterface mIBookInterface;
    private MyCallback myCallback;

    private int count = 1;

    private Disposable disposable;
    private TextView textView;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc);
        findViewById(R.id.messenger).setOnClickListener(v -> {
            Intent intent = new Intent(IpcActivity.this, MessengerService.class);
            isConnectionRegistered = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        });
        findViewById(R.id.aidl).setOnClickListener(v -> {
            Log.e(TAG, "onViewClicked() called");
            new Thread(this::startBookManger).start();

        });
        findViewById(R.id.add_book).setOnClickListener(v -> {
            count++;
            Book book = new Book(count, "人类群星闪耀时" + count);
            try {
                Log.e(TAG, "onViewClicked() called add book " + book);
                mIBookInterface.addBook(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.get_book).setOnClickListener(v -> {
            try {
                List<Book> books = mIBookInterface.getBookList();
                Log.e(TAG, "onViewClicked() called get book " + books);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.del_book).setOnClickListener(v -> {
            try {
                Book book = new Book(count, "人类群星闪耀时" + count);
                Log.e(TAG, "onViewClicked() called delete book " + book);
                mIBookInterface.deleteBook(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.async_callback).setOnClickListener(v -> {
            disposable = Observable.interval(1, TimeUnit.SECONDS)
                    .doOnNext(aLong -> {
                        count++;
                        Book book = new Book(count, "人类群星闪耀时" + count);
                        try {
                            Log.e(TAG, "onViewClicked() called add book to repo " + book);
                            mIBookInterface.addBookToRepo(book);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }).subscribe();

        });
        textView = findViewById(R.id.async_result);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private static class MyCallback extends IBookInfoCallback.Stub {

        private WeakReference<IpcActivity> activityWeakReference;

        MyCallback(IpcActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void notifyBookInfo(List<Book> books) {
            IpcActivity activity = activityWeakReference.get();
            if (activity != null) {
                Log.e(TAG, "On Thread " + Thread.currentThread().getName()
                        + ",notifyBookInfo() called with: books = [" + books + "] "
                );
                activity.mainHandler.post(() -> activity.textView.setText(books.toString()));
            }
        }

        @Override
        public void operationSuccess(String action) {
            Log.e(TAG, "On Thread " + Thread.currentThread().getName()
                    + ",operationSuccess() called with: action = [" + action + "]"
            );
        }
    }

    private final ServiceConnection mBookconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBookInterface = IBookInterface.Stub.asInterface(service);
            Log.e(TAG, "onServiceConnected: thread on "
                    + Thread.currentThread().getName()
            );
            if (myCallback == null) {
                myCallback = new MyCallback(IpcActivity.this);
            }
            try {
                mIBookInterface.registerCallback(myCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
            releaseCallback();
        }
    };

    private void startBookManger() {
        Log.e(TAG, "startBookManger() called");
        Intent intent = new Intent(this, BookManagerService.class);
        isBookServiceRegistered = bindService(intent, mBookconn, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
        if (isConnectionRegistered) {
            unbindService(mConnection);
        }
        if (isBookServiceRegistered) {
            unbindService(mBookconn);
        }
        releaseCallback();
        Log.e(TAG, "onDestroy() called");
    }

    private void releaseCallback() {
        if (mIBookInterface != null && myCallback != null) {
            try {
                mIBookInterface.unregisterCallback(myCallback);
                myCallback = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }
}