package com.engineer.android.mini.util

import android.content.Context
import android.os.PowerManager
import android.util.Log

object PowerManagerUtil {
    private val TAG = "PowerManagerUtil"


    fun wakeupScreen(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "Mini:WakeLock"
        )
        wakeLock.acquire(3000) // 3秒后自动释放
        Log.d(TAG, "Screen woken up")
    }

}