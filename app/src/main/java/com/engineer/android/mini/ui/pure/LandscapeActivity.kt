package com.engineer.android.mini.ui.pure

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
    private val TAG = "LandscapeActivity_TAG"

    private lateinit var controller: WindowInsetsControllerCompat
    private lateinit var backgroundImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called ")

        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.acitivity_landscape)
        backgroundImageView = findViewById(R.id.background_img)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            backgroundImageView.setImageResource(R.drawable.wallpaper_portrait)
        } else {
            backgroundImageView.setImageResource(R.drawable.wallpaper_landscape)
        }

        controller = WindowInsetsControllerCompat(window, window.decorView)

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
                switch.text = "白色状态栏"
            } else {
                controller.isAppearanceLightStatusBars = true
                switch.text = "黑色状态栏"
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

        findViewById<RadioGroup>(R.id.mode_group).setOnCheckedChangeListener { group, checkedId ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return@setOnCheckedChangeListener
            }
            val params = window.attributes

            when (checkedId) {
                R.id.default_way -> {
                    params.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
                }
                R.id.short_edge_way -> {
                    params.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                R.id.never_way -> {
                    params.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
                }
            }
            window.attributes = params
        }
    }

}