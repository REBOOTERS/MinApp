package com.engineer.android.mini.ui.behavior.lifecycle

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.ext.gotoActivity

/**
 * Created on 2020/12/24.
 * @author rookie
 */

open class BaseActivity : AppCompatActivity() {
    private val TAG = this::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideView())
        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart() called")
    }

    open fun provideView(): View = FrameLayout(this)
}

class ActivityA : BaseActivity() {
    override fun provideView(): View {
        val frameLayout = FrameLayout(this)
        frameLayout.setBackgroundColor(Color.MAGENTA)
        val button = Button(this)
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.CENTER
        button.text = "1"
        button.setOnClickListener {
            gotoActivity(ActivityB::class.java)
        }
        frameLayout.addView(button, param)
        return frameLayout
    }
}

class ActivityB : BaseActivity() {
    override fun provideView(): View {
        val frameLayout = FrameLayout(this)
        frameLayout.setBackgroundColor(Color.DKGRAY)
        val button = Button(this)
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.CENTER
        button.text = "2"
        button.setOnClickListener {
            finish()
        }
        frameLayout.addView(button, param)
        return frameLayout
    }
}