package com.engineer.android.mini.ui.behavior

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.engineer.android.mini.databinding.ActivityArtsBinding

class ArtsActivity : AppCompatActivity() {
    private val TAG = "ArtsActivity"
    private val path = "file:///android_asset/arts/tree/index.html";
    private lateinit var viewBinding: ActivityArtsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityArtsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initPureWindow()
        webViewInit()

        viewBinding.webView.loadUrl(path)
    }

    private fun initPureWindow() {
        window.statusBarColor = Color.TRANSPARENT
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.isAppearanceLightStatusBars = true
        controller.hide(WindowInsetsCompat.Type.statusBars())
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val params = window.attributes
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = params
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webViewInit() {
        WebView.setWebContentsDebuggingEnabled(true)
        val settings = viewBinding.webView.settings
        settings.javaScriptEnabled = true

        viewBinding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
//                view?.reload()
                Log.d(TAG, "onPageFinished() called with: view = $view, url = $url")
            }
        }

        viewBinding.root.setOnClickListener { _ ->
            viewBinding.webView.reload()
        }
    }
}