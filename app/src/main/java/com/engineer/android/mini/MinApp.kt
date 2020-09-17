package com.engineer.android.mini

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

/**
 * Created on 2020/9/13.
 * @author rookie
 */
class MinApp : Application() {

    companion object {
        lateinit var INSTANCE: Application
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}