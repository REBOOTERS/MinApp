package com.engineer.android.mini

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.WorkManager
import com.engineer.common.utils.AndroidSystem
import com.engineer.common.utils.ApplySigningUtils
import com.facebook.stetho.Stetho
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

/**
 * Created on 2020/9/13.
 * @author rookie
 */
@HiltAndroidApp
class MinApp : Application() {

    companion object {
        lateinit var INSTANCE: Application
        val MINI = "mini_tag"
        val TAG = "MinApp_TAG"

        val FLUTTER_ENGINE_ID = "FLUTTER_ENGINE_ID"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called start")
        INSTANCE = this
        Stetho.initializeWithDefaults(this)
        DynamicColors.applyToActivitiesIfAvailable(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        appLifecycle()
        val defaultExc = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e(MINI, "current process    : ${Process.myPid()}")
            Log.e(MINI, "current thread     : id = ${t.id}, name = ${t.name}")
            Log.e(MINI, "current exception  : ${Log.getStackTraceString(e)}")
            defaultExc?.uncaughtException(t, e)
        }

        if (BuildConfig.DEBUG) {
            Log.e(MINI, "flavor is = ${BuildConfig.FLAVOR}")
        }
        applicationProcessInfo()
        Log.d(TAG, "onCreate() called end")

        if (WorkManager.isInitialized().not()) {
//            triggerWork(INSTANCE)
        }

        val sign = ApplySigningUtils.getRawSignatureStr(this, this.packageName)
        Log.e(TAG, "sign is $sign")
    }

    private fun isMainProcess(): Boolean {
        val mainPackageName = packageName
        val am: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processList = am.runningAppProcesses
        processList.forEach {
            if (it.processName == mainPackageName) {
                return true
            }
        }
        return false
    }

    private fun applicationProcessInfo() {
        val am: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processList = am.runningAppProcesses
        val mainPackageName = packageName
        val pid = Process.myPid()
        Log.e("ProcessInfo", "applicationProcessInfo() called")
        Log.e("ProcessInfo", "mainPackageName $mainPackageName")
        Log.e("ProcessInfo", "pid             $pid")
        for ((index, value) in processList.withIndex()) {
            Log.e(
                "ProcessInfo",
                "index = $index, value =${value.processName},${value.pid}" + ",${value.uid},${value.lastTrimLevel}"
            )
            value.pkgList.forEachIndexed { i, s ->
                Log.e("ProcessInfo", "i=$i,s=$s")
            }
        }
        Log.e(
            "ProcessInfo", "process pid " + Process.myPid() + ",is64Bit= " + Process.is64Bit()
        )
        Log.d("ProcessInfo", "is mainProcess ${isMainProcess()}")
    }

    private fun appLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                Log.e("ProcessLife", "ApplicationObserver: app moved to foreground")
                Log.e(
                    "ProcessLife",
                    "is Foreground App ${AndroidSystem.isAppForeground(INSTANCE)}, foregroundApp is ${
                        AndroidSystem.getForegroundApp(
                            INSTANCE
                        )
                    }"
                )
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                Log.e("ProcessLife", "ApplicationObserver: app moved to background")
                Log.e(
                    "ProcessLife",
                    "is Foreground App ${AndroidSystem.isAppForeground(INSTANCE)}, foregroundApp is ${
                        AndroidSystem.getForegroundApp(
                            INSTANCE
                        )
                    }"
                )
            }
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                Log.e(
                    "ProcessLife", "onStateChanged() called with: source = $source, event = $event"
                )
            }
        })
    }


    override fun onLowMemory() {
        super.onLowMemory()
        Log.d(TAG, "onLowMemory() called")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(TAG, "onTrimMemory() called with: level = $level")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "onTerminate() called")
    }
}