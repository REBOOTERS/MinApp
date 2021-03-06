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
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setMargins
import androidx.core.widget.NestedScrollView
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.gotoActivity
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import java.io.Serializable
import kotlin.random.Random


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
//        Log.e(TAG, "onPostCreate() called with: savedInstanceState = $savedInstanceState")
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
//        Log.e(TAG, "onPostResume: ")
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

    protected fun randomColor(): Int {
        val r = Random.nextInt(255)
        val g = Random.nextInt(255)
        val b = Random.nextInt(255)
        Log.e("randomColor", "randomColor: r=$r,g=$g,b=$b")
        return Color.rgb(r, g, b)
    }

    open fun provideView(): View = FrameLayout(this)
}

class PanelActivity : BaseLifeActivity() {
    @SuppressLint("SetTextI18n")
    override fun provideView(): View {
        val nestedScrollView = NestedScrollView(this)
        val contentView = LinearLayout(this)
        contentView.orientation = LinearLayout.VERTICAL
        contentView.setBackgroundColor(randomColor())
        contentView.setPadding(0, 48.dp, 0, 0)
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.gravity = Gravity.CENTER_HORIZONTAL
        param.setMargins(10.dp)
        val button = Button(this)
        button.text = "standard"
        button.setOnClickListener {
            val intent = Intent(this, StandardActivity::class.java)
            intent.putExtra("data", BigData())
            gotoActivity(intent)
        }
        contentView.addView(button, param)

        val button01 = Button(this)
        button01.text = "singleTop"
        button01.setOnClickListener {
            gotoActivity(SingleTopActivity::class.java)
        }
        contentView.addView(button01, param)

        val button02 = Button(this)
        button02.text = "singleTask"
        button02.setOnClickListener {
            gotoActivity(SingleTaskActivity::class.java)
        }
        contentView.addView(button02, param)

        val button03 = Button(this)
        button03.text = "singleInstance"
        button03.setOnClickListener {
            gotoActivity(SingleTaskActivity::class.java)
        }
        contentView.addView(button03, param)

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

        val button5 = Button(this)
        button5.text = "open dialog"
        button5.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Just Dialog")
                .setPositiveButton("OK") { _, _ -> }
                .setNegativeButton("Cancel") { _, _ -> }
                .show()
        }
        contentView.addView(button5, param)

        val button6 = Button(this)
        button6.text = "show toast"
        button6.setOnClickListener {
            "I'm Just A Toast".toast()

            OpenRecentHelper.open()
        }
        contentView.addView(button6, param)
        nestedScrollView.addView(contentView)
        return nestedScrollView
    }
}

class StandardActivity : BaseLifeActivity() {
    override fun provideView(): View {
        val cont = LinearLayout(this)
        cont.orientation = LinearLayout.VERTICAL
        cont.setBackgroundColor(randomColor())

        val p = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        cont.layoutParams = p
        cont.gravity = Gravity.CENTER

        val button = Button(this)
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        button.setOnClickListener {
            finish()
        }
        param.gravity = Gravity.CENTER
        button.text = this::class.java.simpleName
        cont.addView(button, param)

        val button1 = Button(this)
        button1.setOnClickListener {
            gotoPage(SingleTopActivity::class.java)
        }
        button1.text = "Start A SingleTop"
        cont.addView(button1, param)


        return cont
    }
}

class SingleTopActivity : BaseLifeActivity() {
    override fun provideView(): View {
        val cont = LinearLayout(this)
        cont.orientation = LinearLayout.VERTICAL
        cont.setBackgroundColor(randomColor())

        val p = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        cont.layoutParams = p
        cont.gravity = Gravity.CENTER
        Log.e("ddd", "con-> ${cont.layoutParams?.height}")
        val button = Button(this)
        val param = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        button.text = this::class.java.simpleName
        button.setOnClickListener {
            finish()
        }
        cont.addView(button, param)

        val bb1 = Button(this)
        bb1.text = "start-self " + hashCode()
//        param.topMargin = 10.dp
        bb1.setOnClickListener {
            gotoPage(SingleTopActivity::class.java)
        }
        cont.addView(bb1, param)

        val bb2 = Button(this)
        bb2.text = "start-a-normal"
        bb2.setOnClickListener {
            gotoPage(StandardActivity::class.java)
        }
        cont.addView(bb2, param)

        return cont
    }
}

class SingleTaskActivity : BaseLifeActivity() {
    override fun provideView(): View {
        val frameLayout = LinearLayout(this)
        frameLayout.orientation = LinearLayout.VERTICAL
        frameLayout.setBackgroundColor(randomColor())
        frameLayout.gravity = Gravity.CENTER
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

        val bb = Button(this)
        bb.text = "start standard"
        bb.setOnClickListener { gotoPage(StandardActivity::class.java) }
        frameLayout.addView(bb, param)
        return frameLayout
    }
}

class SingleInstanceActivity : BaseLifeActivity() {
    override fun provideView(): View {
        val frameLayout = LinearLayout(this)
        frameLayout.orientation = LinearLayout.VERTICAL
        frameLayout.setBackgroundColor(randomColor())
        frameLayout.gravity = Gravity.CENTER
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

        val bb = Button(this)
        bb.text = "start standard"
        bb.setOnClickListener { gotoPage(StandardActivity::class.java) }
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
        frameLayout.setBackgroundColor(randomColor())
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

class ActivityE : BaseLifeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}

class BigData : Serializable {

    @Transient
    private val bytes = ByteArray(1024*1024)
}