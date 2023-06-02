package com.engineer.android.mini.ui.behavior

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class MiniReceiver : BroadcastReceiver() {

    private val TAG = "MiniReceiver_TAG"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: $intent")
        val name = intent.getStringExtra("name")
        Log.i(TAG, "name is $name")
    }
}