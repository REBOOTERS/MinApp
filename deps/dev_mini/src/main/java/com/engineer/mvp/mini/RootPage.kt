package com.engineer.mvp.mini

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.engineer.mvp.mini.focus.RecyclerViewPage
import com.engineer.mvp.mini.nav.NavigationActivity

class RootPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setupView())
    }

    private fun setupView(): ViewGroup {
        val rootView = LinearLayout(this)
        rootView.orientation = LinearLayout.VERTICAL
        val p = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(80, 80, 80, 40)
        }

        val btn_focus = Button(this).apply {
            text = "focus_demo"
            setOnClickListener {
                startActivity(Intent(this@RootPage, RecyclerViewPage::class.java))

            }
            setBackgroundResource(R.color.teal_700)
        }

        rootView.addView(btn_focus, p)

        val navigationDemo = Button(this).apply {
            text = "navigation_demo"
            setOnClickListener {
                startActivity(Intent(this@RootPage, NavigationActivity::class.java))
            }
            setBackgroundResource(R.color.teal_700)
        }
        rootView.addView(navigationDemo, p)
        return rootView
    }
}