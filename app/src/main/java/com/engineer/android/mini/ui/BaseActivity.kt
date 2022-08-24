package com.engineer.android.mini.ui

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat

/**
 * Created on 2020/9/17.
 * @author rookie
 */
open class BaseActivity : AppCompatActivity() {

    internal open var TAG = "TAG_" + this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusBar()
        fullStatusBar()
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

    /**
     * 透明状态栏
     */
    protected fun transparentStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.TRANSPARENT
    }

    /**
     * 内容填空状态栏
     */
    protected fun fullStatusBar() {

        val window = window
        val decorView = window.decorView
        decorView.setOnApplyWindowInsetsListener { v, insets ->
            val defaultInsets = v.onApplyWindowInsets(insets)
            defaultInsets?.let {

                val left = it.systemWindowInsetLeft
                val top = it.systemWindowInsetTop
                val right = it.systemWindowInsetRight
                val bottom = it.systemWindowInsetBottom
//                Log.e(TAG, "insets: $left,$top,$right,$bottom")
            }

            defaultInsets.replaceSystemWindowInsets(
                defaultInsets.systemWindowInsetLeft,
                0,
                defaultInsets.systemWindowInsetRight,
                defaultInsets.systemWindowInsetBottom

            )
        }
        ViewCompat.requestApplyInsets(decorView)
    }

    fun printAny(vararg strs: String) {
        strs.forEach {
            Log.e(TAG, it)
        }
    }
}