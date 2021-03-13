package com.engineer.android.mini

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.collection.LruCache
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.facebook.stetho.Stetho
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

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

    private lateinit var flutterEngine: FlutterEngine


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        Stetho.initializeWithDefaults(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        appLifecycle()
        lruTest()

        // Instantiate a FlutterEngine.
        flutterEngine = FlutterEngine(this)
        // Start executing Dart code to pre-warm the FlutterEngine.
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        // Cache the FlutterEngine to be used by FlutterActivity.
        FlutterEngineCache
            .getInstance()
            .put(FLUTTER_ENGINE_ID, flutterEngine)
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