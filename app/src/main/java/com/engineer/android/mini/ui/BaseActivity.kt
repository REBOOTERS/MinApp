package com.engineer.android.mini.ui

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Created on 2020/9/17.
 * @author rookie
 */
open class BaseActivity : AppCompatActivity() {

    internal open var TAG = "TAG_" + this::class.java.simpleName

    lateinit var controller: WindowInsetsControllerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.TRANSPARENT
        controller = WindowInsetsControllerCompat(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onResume() {
        super.onResume()
        Looper.getMainLooper().queue.addIdleHandler {
            reportFullyDrawn()
            false
        }
    }

    internal fun isNightMode(): Boolean {
        val flag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }


    fun printAny(vararg strs: String) {
        strs.forEach {
            Log.e(TAG, it)
        }
    }
}