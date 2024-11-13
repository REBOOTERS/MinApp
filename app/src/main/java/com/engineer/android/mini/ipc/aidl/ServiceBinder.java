package com.engineer.android.mini.ipc.aidl;

import android.content.Context;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.engineer.common.utils.OpenTaskManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class ServiceBinder extends IBookInterface.Stub {
    private static final String TAG = "ServiceBinder";

    private final CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private IBookInfoCallback iBookInfoCallback;

    public CopyOnWriteArrayList<Book> provideBookList() {
        return mBookList;
    }

    private Context mContext;

    private RemoteCallbackList<IResponseListener> onResponseListeners = new RemoteCallbackList<>();


    public ServiceBinder(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }

    @Override
    public List<Book> getBookList() {
        Log.e(TAG, "getBookList() called on Thread " + Thread.currentThread().getName() + ",id = " + Thread.currentThread().getId());
        return mBookList;
    }

    @Override
    public void addBook(Book book) {
        Log.e(TAG, "addBook() called with: book = [" + book + "]" + " called on Thread " + Thread.currentThread().getName() + ",id = " + Thread.currentThread().getId());

        mBookList.add(book);
    }

    @Override
    public void deleteBook(Book book) {
        Log.e(TAG, "deleteBook() called with: book = [" + book + "]" + " called on Thread " + Thread.currentThread().getName() + ",id = " + Thread.currentThread().getId());
        mBookList.remove(book);
    }

    @Override
    public void registerCallback(IBookInfoCallback callback) {
        iBookInfoCallback = callback;
    }

    @Override
    public void unregisterCallback(IBookInfoCallback callback) {
        iBookInfoCallback = null;
    }


    @Override
    public void addBookToRepo(Book book) {
        Observable.just(5, TimeUnit.SECONDS).doOnComplete(() -> {
            Log.e(TAG, "mBinder.pingBinder() = " + pingBinder() + ",iBookInfoCallback = " + iBookInfoCallback);
            if (pingBinder()) {
                if (iBookInfoCallback != null) {
                    mBookList.add(book);
                    iBookInfoCallback.operationSuccess("addBookToRepo");
                    iBookInfoCallback.notifyBookInfo(mBookList);
                } else {
                    Log.e(TAG, "iBookInfoCallback is null, just return");
                }
            } else {
                Log.e(TAG, "run() called " + isBinderAlive());
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

    @Override
    public void startOtherAppService() {
        Log.e(TAG, "process pid " + Process.myPid() + ",is64Bit= " + Process.is64Bit());
        OpenTaskManager.startThirdAppService(mContext, false);
    }

    @Override
    public void startOtherApp() {
        OpenTaskManager.startOtherApp(mContext);
    }

    @Override
    public void startRequest(String param) throws RemoteException {
        int a = 1;
        while (a < 5) {
            handleResponseListeners(param, a);
            a++;
        }
        Log.d(TAG, "\n");
    }

    private void handleResponseListeners(String param, int result) {
        try {
            Log.d(TAG, param + "-> broadcast begin");
            int cb = onResponseListeners.beginBroadcast();
            for (int i = 0; i < cb; i++) {
                onResponseListeners.getBroadcastItem(i).onSuccess(String.valueOf(result));
            }
            onResponseListeners.finishBroadcast();
            Log.d(TAG, param + "<- broadcast finish");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void registerIResponseListener(IResponseListener response) throws RemoteException {
        onResponseListeners.register(response);
    }

    @Override
    public void unRegisterIResponseListener(IResponseListener response) throws RemoteException {
        onResponseListeners.unregister(response);
    }
}
