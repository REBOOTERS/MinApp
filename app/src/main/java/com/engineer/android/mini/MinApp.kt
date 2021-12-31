package com.engineer.android.mini

import android.app.Application
import android.os.Process
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.LruCache
import androidx.lifecycle.*
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp

/**
 * Created on 2020/9/13.
 * @author rookie
 */
@HiltAndroidApp
class MinApp : Application() {

    companion object {
        lateinit var INSTANCE: Application
        val MINI = "mini"

        val FLUTTER_ENGINE_ID = "FLUTTER_ENGINE_ID"
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        Stetho.initializeWithDefaults(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        appLifecycle()
        lruTest()
        val defaultExc = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Log.e(MINI, "current process    : ${Process.myPid()}")
            Log.e(MINI, "current thread     : id = ${t.id}, name = ${t.name}")
            Log.e(MINI, "current exception  : ${Log.getStackTraceString(e)}")
            defaultExc?.uncaughtException(t, e)
        }
    }


    private fun appLifecycle() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            private fun onAppForeground() {
                Log.e("ProcessLife", "ApplicationObserver: app moved to foreground")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            private fun onAppBackground() {
                Log.e("ProcessLife", "ApplicationObserver: app moved to background")
            }
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                Log.e(
                    "ProcessLife",
                    "onStateChanged() called with: source = $source, event = $event"
                )
            }

        })
    }

    private fun lruTest() {
        val lruCache = LruCache<Int, Char>(10)
        for (i in 65..90) {
            lruCache.put(i, i.toChar())
        }
        Log.e(MINI, "lruTest() called $lruCache")
        val map = lruCache.snapshot()
        map.keys.forEach {
            Log.e(MINI, "key=$it,value=${lruCache[it]}")
        }

        val range = 0..10
        Log.d("what", range.javaClass.name)
        val range1 = 0 until 10
        Log.d("what", range1.javaClass.name)


    }
}