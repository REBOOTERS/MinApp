package com.engineer.android.mini.ui

import android.Manifest
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.engineer.android.mini.ui.widget.FloatingViewHelper
import com.permissionx.guolindev.PermissionX

/**
 * Created on 2020/9/17.
 * @author rookie
 */
open class BaseActivity : AppCompatActivity() {

    internal open var TAG = "TAG_" + this::class.java.simpleName

    lateinit var controller: WindowInsetsControllerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.TRANSPARENT
        controller = WindowInsetsControllerCompat(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    fun addAnyView() {
        FloatingViewHelper().addAnyView(window, this)
    }

    override fun onResume() {
        super.onResume()
        Looper.getMainLooper().queue.addIdleHandler {
            reportFullyDrawn()
            false
        }
    }

    internal fun isNightMode(): Boolean {
        val flag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

    fun showStatusBar(show: Boolean) {
        if (show) {
            controller.show(WindowInsetsCompat.Type.statusBars())
        } else {
            controller.hide(WindowInsetsCompat.Type.statusBars())
        }
    }

    fun printAny(vararg strs: String) {
        strs.forEach {
            Log.e(TAG, it)
        }
    }

    fun requestMediaPermission(trigger: () -> Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        PermissionX.init(this).permissions(permission).request { allGranted, _, _ ->
            if (allGranted) {
                trigger()
            }
        }
    }
}