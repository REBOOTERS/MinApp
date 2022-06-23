package com.engineer.android.mini.ui.behavior

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.*
import java.net.InetAddress

private const val TAG = "WebViewPlayground"

class WebViewActivity : AppCompatActivity() {
    private val prefix = "https://"
    private val host = "www.baidu.com"
    private var targetUrl = prefix + host


    private val mainScope = MainScope()

    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
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
                Log.d(TAG, "onReceivedTitle() called with: title = $title")
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(
                    TAG,
                    "onPageStarted() called with: url = $url, favicon = $favicon"
                )
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished() called with: url = $url")
                WebResourceCacheManager.downloadResource(this@WebViewActivity, targetUrl)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
//                Log.i(TAG, "shouldInterceptRequest() called with: request = ${request?.url}")
                val url = request?.url?.toString() ?: ""
                Log.e(TAG, WebResourceCacheManager.getMimeTypeFromUrl(url))
                if ((request?.url ?: "") == Uri.parse(targetUrl)) {
                    val cache = WebResourceCacheManager.providePath(this@WebViewActivity, targetUrl)
                    Log.e(TAG, "use cache ${cache.length()}")
                    val inputStream = FileInputStream(cache)
                    return WebResourceResponse("text/html", "gzip", inputStream)
                }
                return super.shouldInterceptRequest(view, request)
            }
        }
        webView.loadUrl(targetUrl)

        mainScope.launch {
            printSomeInfo()
        }
    }

    private suspend fun printSomeInfo() {
        val TAG = "DNS"
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

object WebResourceCacheManager {

    fun getMimeTypeFromUrl(url: String): String {
        return MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url)) ?: ""
    }

    fun providePath(context: Context, url: String): File {
        val dir = context.cacheDir.absolutePath
        val name = url.hashCode().toString()

        val fileDir = File(dir)
        if (fileDir.exists().not()) {
            fileDir.mkdir()
        }
        return File(dir, name)
    }

    fun downloadResource(context: Context, url: String) {

        val mimeType = getMimeTypeFromUrl(url)

        Log.d(TAG, "downloadResource() called with: mimeType = $mimeType, url = $url ")

        val dir = context.cacheDir
        val dirPath = dir.absolutePath
        val fileDir = File(dirPath)
        if (fileDir.exists().not()) {
            fileDir.mkdir()
        }

        val fileName = url.hashCode().toString()
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient.Builder().build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body()!!
                val destFile = File(dirPath, fileName)
                var fos: FileOutputStream? = null
                var fis: InputStream? = null
                val buffer = ByteArray(2048)
                try {
                    fos = FileOutputStream(destFile)
                    fis = responseBody.byteStream()
                    var len = fis.read(buffer)
                    while (len != -1) {
                        fos.write(buffer, 0, len)
                        len = fis.read(buffer)
                    }
                } catch (e: Exception) {

                } finally {
                    fos?.close()
                    fis?.close()
                }
            }
        })
    }
}
