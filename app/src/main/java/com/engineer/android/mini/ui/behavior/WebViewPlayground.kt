package com.engineer.android.mini.ui.behavior

import android.app.Application
import android.content.Context
import android.content.MutableContextWrapper
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.util.*

private const val TAG = "WebViewPlayground"

class WebViewActivity : AppCompatActivity() {
    private val prefix = "https://"
    private val host = "www.baidu.com"
    private var targetUrl = prefix + host


    private val mainScope = MainScope()

    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebViewCacheHolder.acquireWebViewInternal(this)
        Log.e(TAG, "webview is ${webView.hashCode()}")
        setContentView(webView)
//        setContentView(R.layout.activity_web_view)
//        webView = findViewById(R.id.web_view)

        val settints = webView.settings
        settints.javaScriptEnabled = true

        targetUrl = "http://xiaodu.baidu.com/saiya/ad_landing_page/vip.meishubao.com/index.html?" +
                "landing_page_id=LANDING-PAGE-MSB&ad_id=msb_landing_page&channel=homefeed" +
                "&idea_id=a3cd177f-18f2-17a4-04d0-63cef90f4749&xiaodutools=hide"
        webView.webChromeClient = object : WebChromeClient() {


            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                Log.d(TAG, "onReceivedTitle() called with: view = $view, title = $title")
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(
                    TAG,
                    "onPageStarted() called with: view = $view, url = $url, favicon = $favicon"
                )
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished() called with: view = $view, url = $url")
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
//                Log.d(TAG, "onLoadResource() called with: view = $view, url = $url")
            }
        }
        webView.loadUrl(targetUrl)

        mainScope.launch {
            printSomeInfo()
        }
    }

    private suspend fun printSomeInfo() {
        withContext(Dispatchers.IO) {
            try {
                val ips = InetAddress.getByName(host)
                Log.e(TAG, "onCreate: ${ips.address}")
                Log.e(TAG, "onCreate: ${ips.hostAddress}")
                Log.e(TAG, "onCreate: ${ips.hostName}")
                Log.e(TAG, "onCreate: ${ips.isAnyLocalAddress}")
                Log.e(TAG, "onCreate: ${ips.isLinkLocalAddress}")
                Log.e(TAG, "onCreate: ${ips.isLoopbackAddress}")
                Log.e(TAG, "onCreate: ${ips.isReachable(1)}")
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "error")
            }

        }
    }

}


object WebViewCacheHolder {

    private var webView: WebView? = null


    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application

        webView = createWebView(MutableContextWrapper(WebViewCacheHolder.application))
    }


    fun acquireWebViewInternal(context: Context): WebView {
        Log.d(TAG, "webView = $webView")
        return if (webView == null) {
            createWebView(context)
        } else {
            val contextWrapper = webView!!.context as MutableContextWrapper
            contextWrapper.baseContext = context
            webView!!
        }
    }

    private fun createWebView(context: Context): WebView {
        return WebView(context)
    }

}
