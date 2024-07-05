package com.engineer.android.mini.ui.behavior.lifecycle

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setMargins
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.engineer.android.mini.R
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

    override var TAG = "LifeCycle_" + this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideView())
        Log.e(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
    }


    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume() called")
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

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        Log.e(TAG, "onNewIntent() called with: intent = $intent")
//    }

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "currentProcess pid=" + Process.myPid())
        Log.e(TAG, "currentProcess uid=" + Process.myUid())
        Log.e(TAG, "currentProcess uid_1=" + Process.getUidForName(packageName))
        Log.e(TAG, "currentProcess tid=" + Process.myTid())
        Log.e(TAG, "currentProcess myUserHandle=" + Process.myUserHandle())
        Log.e(TAG, "currentProcess getElapsedCpuTime=" + Process.getElapsedCpuTime())
    }


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

        val button7 = Button(this)
        button7.text = "activity cost test"
        button7.setOnClickListener {
            gotoActivity(ActivityF::class.java)
        }
        contentView.addView(button7, param)

        val button8 = Button(this)
        button8.text = "input on screen-sensor"
        button8.setOnClickListener { gotoActivity(ActivityG::class.java) }
        contentView.addView(button8,param)

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
            gotoActivity(SingleTopActivity::class.java)
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
            gotoActivity(SingleTopActivity::class.java)
        }
        cont.addView(bb1, param)

        val bb2 = Button(this)
        bb2.text = "start-a-normal"
        bb2.setOnClickListener {
            gotoActivity(StandardActivity::class.java)
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
        bb.setOnClickListener { gotoActivity(StandardActivity::class.java) }
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
        bb.setOnClickListener { gotoActivity(StandardActivity::class.java) }
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
        val param2 = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param2.gravity = Gravity.CENTER
        param.topMargin = 200.dp
        val btn = Button(this)
        btn.text = "open audio activity"
        btn.setOnClickListener {
            "on another branch".toast()
        }
        frameLayout.addView(button, param)
        frameLayout.addView(btn, param2)
        return frameLayout
    }
}

class ActivityE : BaseLifeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}

class ActivityF : BaseLifeActivity() {

}

class ActivityG : BaseLifeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        controller.hide(WindowInsetsCompat.Type.navigationBars())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        window.navigationBarColor = Color.TRANSPARENT
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.i(TAG,"onWindowFocusChanged $hasFocus")
    }
    override fun provideView(): View {
        val frameLayout = FrameLayout(this)
        frameLayout.setBackgroundColor(randomColor())
        val inputEditText = EditText(this)
        inputEditText.inputType = EditorInfo.IME_ACTION_SEARCH
        val param = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(40.dp,0,40.dp,0)
        inputEditText.hint = "please input sth"
        param.gravity = Gravity.CENTER
        frameLayout.addView(inputEditText, param)
        val tv = TextView(this)
        tv.setTextColor(Color.BLACK)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,22f)
        tv.text = getString(R.string.long_chinese_content)
        frameLayout.addView(tv)
        val imageView = ImageView(this)
        val param1 = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        Glide.with(this).load(R.drawable.phone).into(imageView)
//        imageView.setImageResource(R.drawable.phone)
//        frameLayout.addView(imageView, param1)
        return frameLayout
    }
}

class BigData : Serializable {

    @Transient
    private val bytes = ByteArray(1024 * 1024)
}