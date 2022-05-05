package com.engineer.android.mini.ipc.aidl

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log

class BinderBridge : ServiceConnection {
    private var mIBookInterface: IBookInterface? = null
    private var myCallback: MyCallback? = null
    var asyncCallback: AsyncProcessCallback? = null

    fun provideIBookInterface() = mIBookInterface


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.e("TGA_TAG", "service is $service")
        mIBookInterface = IBookInterface.Stub.asInterface(service)
        Log.e("TGA_TAG", "interface is $mIBookInterface")
        Log.e(
            "ServiceConnection", "onServiceConnected: thread on "
                    + Thread.currentThread().name
        )
        if (myCallback == null) {
            myCallback = MyCallback()
        }
        try {
            mIBookInterface?.registerCallback(myCallback)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.e("ServiceConnection", "onServiceDisconnected() called with: name = [$name]")
        releaseCallback()
    }

    fun releaseCallback() {
        if (mIBookInterface != null && myCallback != null) {
            try {
                mIBookInterface?.unregisterCallback(myCallback)
                myCallback = null
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    inner class MyCallback : IBookInfoCallback.Stub() {
        override fun notifyBookInfo(books: List<Book>) {
            asyncCallback?.notifyBookInfo(books)
        }

        override fun operationSuccess(action: String) {
            asyncCallback?.operationSuccess(action)
        }

    }

    interface AsyncProcessCallback {
        fun notifyBookInfo(books: List<Book>)
        fun operationSuccess(action: String)
    }
}