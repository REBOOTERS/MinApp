package com.engineer.android.mini.ui.pure

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.view.setMargins
import androidx.core.view.updateLayoutParams
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.dp

class LayoutActivity : AppCompatActivity() {
    private val TAG = "LayoutActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)

        addViewToWindow()
    }

    override fun onResume() {
        super.onResume()
        val window = window
        Log.e(TAG, "onResume() called $window")
    }

    private fun addViewToWindow() {
        val manager: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val child = genChild()
        val params = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
            x = 10.dp
            y = 24.dp
        }
        manager.addView(child, params)
    }

    private fun genChild(): View {
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

        return container
    }

    private fun hack() {
        Log.e(TAG,"start hack ")
        try {
            val clazz = Class.forName("android.view.ViewRootImpl")
            val fileds = clazz.declaredFields
            fileds?.forEach {
                Log.e(TAG, "it name =  ${it.name}, it type = ${it.type}")
            }
        } catch (e: Exception) {
        }
        Log.e(TAG,"end  hack ")
    }


}