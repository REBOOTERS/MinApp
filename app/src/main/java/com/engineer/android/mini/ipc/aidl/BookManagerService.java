package com.engineer.android.mini.ipc.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";

    public BookManagerService() {
    }

    private final CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private IBookInfoCallback iBookInfoCallback;

    private final Binder mBinder = new IBookInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString) throws RemoteException {

        }

        @Override
        public List<Book> getBookList() {
            Log.e(TAG, "getBookList() called on Thread " + Thread.currentThread().getName());
            return mBookList;
        }

        @Override
        public void addBook(Book book) {
            Log.e(TAG, "addBook() called with: book = [" + book + "]" + " called on Thread "
                    + Thread.currentThread().getName());
            mBookList.add(book);
        }

        @Override
        public void deleteBook(Book book) {
            Log.e(TAG, "deleteBook() called with: book = [" + book + "]" + " called on Thread "
                    + Thread.currentThread().getName());
            mBookList.remove(book);
        }

        @Override
        public void registerCallback(IBookInfoCallback callback) {
            iBookInfoCallback = callback;
        }

        @Override
        public void unregisterCallback(IBookInfoCallback callback) {
            callback = null;
        }


        @Override
        public void addBookToRepo(Book book) {
            Observable.timer(2, TimeUnit.SECONDS)
                    .doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                            if (mBinder.isBinderAlive()) {
                                if (iBookInfoCallback != null) {
                                    mBookList.add(book);
                                    iBookInfoCallback.operationSuccess("addBookToRepo");
                                    iBookInfoCallback.notifyBookInfo(mBookList);
                                }
                            } else {
                                Log.d(TAG, "run() called " + mBinder.isBinderAlive());
                            }

                        }
                    }).subscribe();
        }

        @Override
        public List<Book> getBookListFromRepo() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mBookList;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate() called");
        mBookList.add(new Book(100, "Android"));
        mBookList.add(new Book(101, "iOS"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind() called with: intent = [" + intent + "]");
        return mBinder;
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