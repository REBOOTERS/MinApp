package com.engineer.android.mini.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Service
import android.content.Context
import android.util.Log


object AndroidSystem {

    @Volatile
    private var sIsHarmonyOS: Boolean? = null

    fun isHarmonyOS(): Boolean {
        if (sIsHarmonyOS == null) {
            synchronized(AndroidSystem::class.java) {
                sIsHarmonyOS = try {
                    val clz = Class.forName("com.huawei.system.BuildEx")
                    val method = clz.getMethod("getOsBrand")
                    "harmony" == method.invoke(clz)
                } catch (e: Exception) {
                    false
                }
            }
        }
        return sIsHarmonyOS!!
    }

    fun isAppForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcessInfoList = activityManager.runningAppProcesses
        if (runningAppProcessInfoList == null) {
            Log.d("runningAppProcess:", "runningAppProcessInfoList is null!")
            return false
        }
        for (processInfo in runningAppProcessInfoList) {
            if (processInfo.processName == context.packageName
                && processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            ) {
                return true
            }
        }
        return false
    }

    fun getForegroundApp(context: Context): String? {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val lr = am.runningAppProcesses ?: return null
        for (ra in lr) {
            if (ra.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE
                || ra.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            ) {
                return ra.processName
            }
        }
        return null
    }
}