package com.engineer.android.mini.ipc.manager

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 *   package:mine tag:IResponseListenerImpl tag:ServiceBinder tag:ServiceHelper
 */
object ServiceHelper {
    internal const val TAG = "ServiceHelper"
    private const val SERVICE_APP = ""
    private const val SERVICE_FULL_NAME = ""
    internal var bindServiceCallback: BindServiceCallback? = null

    internal var binding = false

    private val bridge = BinderBridge()

    fun provideConnection() = bridge
    fun provideIBookInterface() = bridge.provideIBookInterface()

    fun bindService(
        context: Context, serviceApp: String = SERVICE_APP, serviceName: String = SERVICE_FULL_NAME
    ): Boolean {
        Log.d(TAG, "bindService() called with: context = $context")
        if (binding) {
            Log.d(TAG, "binding == $binding")
            return false
        }
        binding = true
        val intent = Intent()
        intent.putExtra("packageName", context.packageName)
        intent.setComponent(ComponentName(serviceApp, serviceName))
        val result = context.bindService(intent, bridge, Context.BIND_AUTO_CREATE)
        if (result.not()) {
            binding = false
        }
        return result
    }

    fun registerBindServiceCallback(callback: BindServiceCallback) {
        bindServiceCallback = callback
    }

    fun unRegisterBindServiceCallback() {
        bindServiceCallback = null
    }

    fun releaseCallback() {
        bridge.releaseCallback()
    }

}