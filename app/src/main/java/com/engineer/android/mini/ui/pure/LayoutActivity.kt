package com.engineer.android.mini.ui.pure

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Choreographer
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.setMargins
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityLayoutBinding
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.gotoActivity
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity

class LayoutActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLayoutBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        addViewToWindow()

        viewBinding.childOne.setOnClickListener {
            "I'm child_one".toast()
            gotoActivity(EmptyActivity::class.java)
        }

        val looper = Looper.myLooper()
        val messageQueue = Looper.myQueue()
        val mainLooper = Looper.getMainLooper()

        printAny("looper $looper")
        printAny("messageQueue $messageQueue")
        printAny("mainLooper $mainLooper")

        Looper.myQueue().addIdleHandler {
            printAny("idleHandle execute")
            false
        }
    }

    override fun onResume() {
        super.onResume()
        val window = window
        Log.e(TAG, "onResume() called $window")
    }

    private fun addViewToWindow() {
        val manager: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val child = genChild(manager)
        val params = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION
//            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
            x = 10.dp
            y = 24.dp
        }
        manager.addView(child, params)
        viewBinding.childRoot.setOnClickListener(null)
    }

    private fun genChild(windowManager: WindowManager): View {
        val containerShell = HandleBackMenuView(this)
        containerShell.setBackgroundColor(Color.parseColor("#55000000"))
        containerShell.orientation = LinearLayout.VERTICAL

        val container = LinearLayout(this)
        container.setBackgroundResource(R.color.colorAccent)
        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER_HORIZONTAL

        val text = TextView(this)
        text.text = getString(R.string.app_name)
        text.setTextColor(Color.WHITE)
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10.dp)
        container.addView(text, params)

        val image = ImageView(this)
        image.setImageResource(R.drawable.avatar)
        val p = LinearLayout.LayoutParams(50.dp, 50.dp)
        p.setMargins(10.dp)
        container.addView(image, p)

        val rlayout = RelativeLayout(this)
        val pp = rlayout.layoutParams
        Log.e(TAG, "pp is $pp")
        val p1 = LinearLayout.LayoutParams(50.dp, 80.dp)
        p1.setMargins(10.dp)
        rlayout.setBackgroundColor(Color.parseColor("#ffffff"))
        container.addView(rlayout, p1)
        Log.e(TAG, "pp2 is ${rlayout.layoutParams}")

        container.setOnClickListener {
            hack()
        }

        containerShell.handleCallback = object : HandleBackMenuView.HandleBackMenu {
            override fun back(handleBackMenuView: HandleBackMenuView) {
                "remove self from window".toast()
                windowManager.removeView(containerShell)
                viewBinding.childRoot.setOnClickListener {
                    addViewToWindow()
                }
            }

        }
        val p2 = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        p2.setMargins(50.dp)
        containerShell.addView(container, p2)
        return containerShell
    }

    private fun hack() {
        Log.e(TAG, "start hack ")
        try {
            val clazz = Class.forName("android.view.ViewRootImpl")
            val fileds = clazz.declaredFields
            fileds.forEach {
                Log.e(TAG, "it name =  ${it.name}, it type = ${it.type}")
            }
        } catch (e: Exception) {
        }
        Log.e(TAG, "end  hack ")
    }

    class HandleBackMenuView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null, defaultStyle: Int = 0
    ) : LinearLayout(context, attributeSet, defaultStyle) {
        override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
            event?.let {
                if (it.keyCode == KeyEvent.KEYCODE_BACK && it.action == KeyEvent.ACTION_UP) {
                    handleCallback?.back(this)
                    return true
                }
            }
            return super.dispatchKeyEvent(event)
        }

        var handleCallback: HandleBackMenu? = null

        interface HandleBackMenu {
            fun back(handleBackMenuView: HandleBackMenuView)
        }
    }


}


class EmptyActivity : BaseActivity() {

    private lateinit var tv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
        tv = TextView(this)
        tv.text = "111"
        tv.setBackgroundColor(Color.RED)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv.layoutParams = params
        setContentView(tv)

        Choreographer.getInstance().postFrameCallback {}
        Choreographer.getInstance().removeFrameCallback { }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        Log.e(TAG, "onPostCreate() called with: savedInstanceState = $savedInstanceState")
        val view = window.findViewById<View>(android.R.id.content)
        Log.e(TAG, "onPostCreate() called ${view.measuredHeight},${tv.measuredHeight}")

    }

    override fun onResume() {
        super.onResume()
        val view = window.findViewById<View>(android.R.id.content)
        Log.e(TAG, "onResume() called ${view.width},${tv.measuredHeight}")

    }

    override fun onPostResume() {
        super.onPostResume()
        val view = window.findViewById<View>(android.R.id.content)
        Log.e(TAG, "onPostResume() called ${view.width},${tv.measuredHeight}")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.e(TAG, "onWindowFocusChanged() called with: hasFocus = $hasFocus")
        val view = window.findViewById<View>(android.R.id.content)
        Log.e(TAG, "onWindowFocusChanged() called view = ${getViewPos(view)}")
        Log.e(TAG, "onWindowFocusChanged() called tv   = ${getViewPos(tv)}")
    }

    private fun getViewPos(view: View): String {
        val sb = StringBuilder()

        sb.append("====>").append("\n").append("left=").append(view.left).append(" ")
            .append("top=").append(view.top).append(" ")
            .append("right=").append(view.right).append(" ")
            .append("bottom=").append(view.bottom).append("\n")
            .append("width=").append(view.width).append(" ")
            .append("height=").append(view.height).append("\n")
            .append("parent=").append(view.parent).append("\n")

        return sb.toString()
    }
}
