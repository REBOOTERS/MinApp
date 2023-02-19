package com.engineer.android.mini.ui.behavior

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.*
import android.os.Handler.Callback
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.engineer.android.mini.databinding.ActivityFishBinding

class FishActivity : AppCompatActivity() {
    private val TAG = "FishActivity"
    private val path = "file:///android_asset/arts/fish/index.html";
    private lateinit var viewBinding: ActivityFishBinding
    private val handler = Handler(Looper.getMainLooper(), object : Callback {
        override fun handleMessage(msg: Message): Boolean {
            if (msg.what == 1) {
                viewBinding.loading.visibility = View.GONE
                return true
            }
            return false
        }

    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFishBinding.inflate(layoutInflater)
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
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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

        viewBinding.webView.addJavascriptInterface(JsCallback(handler), "jsCallback")

        viewBinding.redraw.setOnClickListener {
            viewBinding.loading.visibility = View.VISIBLE
            viewBinding.webView.evaluateJavascript("javascript:drawSvg()", object : ValueCallback<String> {
                override fun onReceiveValue(value: String?) {
                    Log.d(TAG, "onReceiveValue() called with: value = $value")
                }
            })
        }
        viewBinding.redrawAnim.setOnClickListener {
            viewBinding.webView.evaluateJavascript("javascript:drawAnimSvg()", object : ValueCallback<String> {
                override fun onReceiveValue(value: String?) {
                    Log.d(TAG, "onReceiveValue() called with: value = $value")
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.webView.destroy()
        handler.removeCallbacksAndMessages(null)
    }

    private class JsCallback(val handler: Handler) {
        private val TAG = "FishActivity"
        @JavascriptInterface
        fun onFinish() {
            handler.sendEmptyMessage(1)
            Log.d(TAG, "onFinish() called " + Thread.currentThread().name)
        }
    }
}