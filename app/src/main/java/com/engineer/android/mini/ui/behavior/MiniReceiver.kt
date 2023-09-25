package com.engineer.android.mini.ui.behavior

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.engineer.common.notification.NotificationHelper


class MiniReceiver : BroadcastReceiver() {

    private val TAG = "MiniReceiver_TAG"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: $intent")
        val name = intent.getStringExtra("name")
        Log.i(TAG, "name is $name")

        // adb shell am broadcast -a com.android.mini.receiver -f 0x01000000 -e name notify
        // 可以触发这个广播
        if("notify" == name) {
            NotificationHelper.showNotification(context)
        }
    }
}