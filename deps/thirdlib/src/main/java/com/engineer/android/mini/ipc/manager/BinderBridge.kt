package com.engineer.android.mini.ipc.manager

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.engineer.android.mini.ipc.aidl.Book
import com.engineer.android.mini.ipc.aidl.IBookInfoCallback
import com.engineer.android.mini.ipc.aidl.IBookInterface

class BinderBridge : ServiceConnection {
    private var mIBookInterface: IBookInterface? = null
    private var myCallback: MyCallback? = null
    var asyncCallback: AsyncProcessCallback? = null

    fun provideIBookInterface() = mIBookInterface


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.e(ServiceHelper.TAG, "service is $service")
        Log.e(ServiceHelper.TAG, "interface is $mIBookInterface")
        Log.e(
            ServiceHelper.TAG, "onServiceConnected: thread on " + Thread.currentThread().name
        )
        mIBookInterface = IBookInterface.Stub.asInterface(service)
        mIBookInterface?.let {
            ServiceHelper.bindServiceCallback?.bindSuccess()
        } ?: run {
            ServiceHelper.bindServiceCallback?.bindFail("asInterface return null")
        }
        ServiceHelper.binding = false
        service?.linkToDeath({ ServiceHelper.bindServiceCallback?.bindFail("link to death") }, 0)


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
        Log.e(ServiceHelper.TAG, "onServiceDisconnected() called with: name = [$name]")
        releaseCallback()
        mIBookInterface = null
        ServiceHelper.bindServiceCallback?.bindFail("onServiceDisconnected")
        ServiceHelper.binding = false
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