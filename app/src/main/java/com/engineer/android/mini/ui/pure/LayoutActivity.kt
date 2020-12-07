package com.engineer.android.mini.ui.pure

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.dp

class LayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)

        addViewToWindow()
    }

    private fun addViewToWindow() {
        val manager: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val child = genChild()
        val params = WindowManager.LayoutParams(100.dp, 100.dp)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }

        manager.addView(child, params)
    }

    private fun genChild(): View {
        val text = TextView(this)
        text.text = getString(R.string.app_name)
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        return text
    }


}