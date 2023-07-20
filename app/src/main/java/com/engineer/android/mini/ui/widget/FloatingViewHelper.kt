package com.engineer.android.mini.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import androidx.core.view.contains
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.toast

@SuppressLint("StaticFieldLeak")
class FloatingViewHelper {

    private val TAG = "FloatingViewHelper"

    private var subView: View? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var isShow = false

    private var window: Window? = null

    private val hideTipsTask = Runnable {
        val decorView = window?.decorView
        if (decorView is FrameLayout) {
            subView?.let {
                if (decorView.contains(it)) {
                    decorView.removeView(it)
                    Log.e(TAG, "hide success")
                    isShow = false
                } else {
                    Log.e(TAG, "no such view")
                }
            }
        }
    }


    fun addAnyView(window: Window, context: Context) {
        this.window = window
        if (isShow) {
            Log.e(TAG, "isShow,ignore")
            return
        }
        isShow = true
        val decorView = window.decorView

        if (decorView is FrameLayout) {
            if (subView == null) {
                subView = provideTipsView(context)
            }
            val params = provideLayoutParams()

            decorView.addView(subView, params)
            mainHandler.removeCallbacksAndMessages(null)
            mainHandler.postDelayed(hideTipsTask, 3000)
        }
    }

    private fun provideTipsView(context: Context): View {
        val subview = LayoutInflater.from(context).inflate(R.layout.msg_custom_tips, null)
        subview.setOnClickListener {
            "get click".toast()
        }
        return subview
    }

    private fun provideLayoutParams(): FrameLayout.LayoutParams {
        val params =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_HORIZONTAL
        params.topMargin = 10.dp
        return params
    }
}