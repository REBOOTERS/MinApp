package com.engineer.other.impls

import android.util.Log
import com.engineer.android.mini.ipc.aidl.IResponseListener

object IResponseListenerImpl : IResponseListener.Stub() {
    private const val TAG = "IResponseListenerImpl"
    override fun onSuccess(responseJson: String?) {
        Log.d(TAG, "onSuccess() called with: responseJson = $responseJson")
    }

    override fun onFail(e: String?) {
        Log.d(TAG, "onFail() called with: e = $e")
    }
}