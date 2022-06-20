package com.engineer.android.mini.ui.behavior

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress

class WebViewActivity : AppCompatActivity() {
    private val TAG = "WebViewPlayground"
    private val prefix = "https://"
    private val host = "www.baidu.com"
    private val targetUrl = prefix + host

    private val mainScope = MainScope()

    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        webView = findViewById(R.id.web_view)

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