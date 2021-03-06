package com.engineer.android.mini

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.LruCache
import androidx.lifecycle.*
import com.facebook.stetho.Stetho

/**
 * Created on 2020/9/13.
 * @author rookie
 */
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

        ProcessLifecycleOwner.get().lifecycle.addObserver(object :LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                Log.e("ProcessLife", "onStateChanged() called with: source = $source, event = $event")
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
    }
}