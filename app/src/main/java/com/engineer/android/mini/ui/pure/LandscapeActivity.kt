package com.engineer.android.mini.ui.pure

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.toWindowInsetsControllerCompat
import com.engineer.android.mini.R
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 *  [WindowInsetsControllerCompat]
 *
 *  https://blog.csdn.net/StjunF/article/details/121840122
 *
 *  [WindowInsetsController]
 *  [WindowInsetsCompat]
 *
 *  https://blog.csdn.net/jingzz1/article/details/111468726
 */
class LandscapeActivity : AppCompatActivity() {
    private val TAG = "LandscapeActivity"
    private lateinit var controller: WindowInsetsControllerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.TRANSPARENT
//        window.decorView.systemUiVisibility =
//            (SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        setContentView(R.layout.acitivity_landscape)
        val frameLayout = findViewById<FrameLayout>(R.id.content_landscape);
        controller = WindowInsetsControllerCompat(window, frameLayout)

        controller.isAppearanceLightStatusBars = true
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = Color.TRANSPARENT
        }

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        controller.addOnControllableInsetsChangedListener { controller, typeMask ->
            Log.d(
                TAG,
                "onControllableInsetsChanged() called with: controller = $controller, typeMask = $typeMask"
            )
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        findViewById<SwitchMaterial>(R.id.immersive_switch).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                WindowCompat.setDecorFitsSystemWindows(window, true)
                buttonView.text = "沉浸式-关"
            } else {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                buttonView.text = "沉浸式-开"
            }
        }

        val switch: SwitchMaterial = findViewById(R.id.status_bar_color_switch)
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                controller.isAppearanceLightStatusBars = false
                switch.text = "黑色状态栏"
            } else {
                controller.isAppearanceLightStatusBars = true
                switch.text = "白色状态栏"
            }
        }
        val showSwitch = findViewById<SwitchMaterial>(R.id.status_bar_switch)
        showSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                controller.hide(WindowInsetsCompat.Type.statusBars())
                buttonView.text = "状态栏隐藏"
            } else {
                controller.show(WindowInsetsCompat.Type.statusBars())
                buttonView.text = "状态栏显示"
            }
        }
        findViewById<SwitchMaterial>(R.id.navigation_bar_switch).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                controller.hide(WindowInsetsCompat.Type.navigationBars())
                buttonView.text = "导航栏隐藏"
            } else {
                controller.show(WindowInsetsCompat.Type.navigationBars())
                buttonView.text = "导航栏显示"
            }
        }
    }

}