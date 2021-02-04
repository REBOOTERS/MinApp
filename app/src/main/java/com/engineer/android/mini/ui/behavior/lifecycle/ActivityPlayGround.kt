package com.engineer.android.mini.ui.behavior.lifecycle

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.gotoActivity
import com.engineer.android.mini.ui.BaseActivity

/**
 * Created on 2020/12/24.
 * @author rookie
 */

open class BaseLifeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideView())
        Log.e(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Log.e(TAG, "onPostCreate() called with: savedInstanceState = $savedInstanceState")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume() called")
    }

    override fun onPostResume() {
        super.onPostResume()
        Log.e(TAG, "onPostResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy() called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e(TAG, "onRestart() called")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e(TAG, "onNewIntent() called with: intent = $intent")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.e(TAG, "onConfigurationChanged() called with: newConfig = $newConfig")
    }

    open fun provideView(): View = FrameLayout(this)
}

class ActivityA : BaseLifeActivity() {
    @SuppressLint("SetTextI18n")
    override fun provideView(): View {
        val contentView = LinearLayout(this)
        contentView.orientation = LinearLayout.VERTICAL
        contentView.setBackgroundColor(Color.MAGENTA)
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.CENTER_HORIZONTAL
        param.setMargins(10.dp)
        val button = Button(this)
        button.text = "normal"
        button.setOnClickListener {
            gotoActivity(ActivityB::class.java)
        }
        contentView.addView(button, param)

        val button01 = Button(this)
        button01.text = "singleTop"
        button01.setOnClickListener {
            gotoActivity(ActivityB1::class.java)
        }
        contentView.addView(button01, param)

        val button02 = Button(this)
        button02.text = "singleTask"
        button02.setOnClickListener {
            gotoActivity(ActivityB2::class.java)
        }
        contentView.addView(button02, param)

        val button2 = Button(this)
        button2.text = "transparent"
        button2.setOnClickListener {
            gotoActivity(ActivityC::class.java)
        }
        contentView.addView(button2, param)

        val button3 = Button(this)
        button3.text = "another process"
        button3.setOnClickListener {
            gotoActivity(ActivityD::class.java)
        }
        contentView.addView(button3, param)

        val button4 = Button(this)
        button4.text = "start and finish"
        button4.setOnClickListener {
            gotoActivity(ActivityE::class.java)
        }
        contentView.addView(button4, param)


        return contentView
    }
}

class ActivityB : BaseLifeActivity() {
    override fun provideView(): View {
        val frameLayout = FrameLayout(this)
        frameLayout.setBackgroundColor(Color.YELLOW)
        val button = Button(this)
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.CENTER
        button.text = this::class.java.simpleName
        button.setOnClickListener {
            finish()
        }
        frameLayout.addView(button, param)
        return frameLayout
    }
}

class ActivityB1 : BaseLifeActivity() {
    override fun provideView(): View {
        val frameLayout = FrameLayout(this)
        frameLayout.setBackgroundColor(Color.YELLOW)
        val button = Button(this)
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.CENTER
        button.text = this::class.java.simpleName
        button.setOnClickListener {
            finish()
        }
        frameLayout.addView(button, param)
        return frameLayout
    }
}

class ActivityB2 : BaseLifeActivity() {
    override fun provideView(): View {
        val frameLayout = FrameLayout(this)
        frameLayout.setBackgroundColor(Color.YELLOW)
        val button = Button(this)
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.CENTER
        button.text = this::class.java.simpleName
        button.setOnClickListener {
            finish()
        }
        frameLayout.addView(button, param)
        return frameLayout
    }
}

class ActivityC : BaseLifeActivity() {

    override fun provideView(): View {
        val frameLayout = FrameLayout(this)
        frameLayout.setBackgroundColor(Color.parseColor("#66000000"))
        val button = Button(this)
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.CENTER
        button.text = this::class.java.simpleName
        button.setOnClickListener {
            finish()
        }
        frameLayout.addView(button, param)

        val text = TextView(this)
        text.text = "透明的 Activity"
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 46f)
        text.setTextColor(Color.WHITE)

        val param1 = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param1.topMargin = 16.dp
        param1.gravity = Gravity.CENTER_HORIZONTAL
        frameLayout.addView(text, param1)
        return frameLayout
    }
}

class ActivityD : BaseLifeActivity() {

    override fun provideView(): View {
        val frameLayout = FrameLayout(this)
        frameLayout.setBackgroundColor(Color.CYAN)
        val button = Button(this)
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.CENTER
        button.text = this::class.java.simpleName
        button.setOnClickListener {
            finish()
        }
        frameLayout.addView(button, param)
        return frameLayout
    }
}

class ActivityE: BaseLifeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}