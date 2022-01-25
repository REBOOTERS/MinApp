package com.engineer.android.mini.ipc.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";

    public BookManagerService() {
    }

    private final CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

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
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(100, "Android"));
        mBookList.add(new Book(101, "iOS"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}