package com.engineer.android.mini.ui.behavior

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.MimeTypeMap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.InetAddress

private const val TAG = "WebViewPlayground"

class WebViewActivity : AppCompatActivity() {
    private val prefix = "https://"
    private val host = "www.baidu.com"
    private var targetUrl = prefix + host


    private val mainScope = MainScope()

    private lateinit var webView: WebView

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
        webView = WebView(this)
        webView.setBackgroundColor(Color.WHITE)
        webView.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        webView.addJavascriptInterface(WebInterface(this), "Android")
        Log.e(TAG, "webview is ${webView.hashCode()}")


        val clearCacheBtn = Button(this)
        val downloadCacheBtn = Button(this)

        clearCacheBtn.setOnClickListener {
            WebResourceCacheManager.clearAll(this)
        }
        downloadCacheBtn.setOnClickListener {
            WebResourceCacheManager.downloadResource(this@WebViewActivity, targetUrl)
        }
        val frameLayout = FrameLayout(this)
        frameLayout.setBackgroundColor(Color.RED)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        frameLayout.addView(webView, params)
        val params1 = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params1.gravity = Gravity.BOTTOM or Gravity.END
        params1.marginEnd = 30.dp
        params1.bottomMargin = 50.dp
        clearCacheBtn.text = "clear cache"
        frameLayout.addView(clearCacheBtn, params1)
        val params2 = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params2.gravity = Gravity.BOTTOM or Gravity.END
        params2.marginEnd = 30.dp
        params2.bottomMargin = 100.dp
        downloadCacheBtn.text = "download cache"
        frameLayout.addView(downloadCacheBtn, params2)
        setContentView(frameLayout)

        WebView.setWebContentsDebuggingEnabled(true)

        targetUrl = "https://images.pexels.com/photos/11163123/pexels-photo-11163123.jpeg"

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
                    TAG, "onPageStarted() called with: url = $url, favicon = $favicon"
                )
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished() called with: url = $url")
                "onPageFinished".toast()

//                val javascript =
//                    "javascript:(function() { " + "var targetElement = document.querySelector('img'); " + "if (targetElement) { " + "   targetElement.style.display='block'; " + "   targetElement.style.position='absolute'; " + "   targetElement.style.top='0'; " + "   targetElement.style.left='0'; " + "   document.body.appendChild(targetElement); " + "} " + "})()"
//                view?.loadUrl(javascript)

                view?.loadUrl("javascript:window.Android.getHtml(document.documentElement.outerHTML);");

            }

            override fun shouldInterceptRequest(
                view: WebView?, request: WebResourceRequest?
            ): WebResourceResponse? {
                val url = request?.url?.toString() ?: ""
                Log.e(TAG, WebResourceCacheManager.getMimeTypeFromUrl(url) + ",url is $url")
                if ((request?.url ?: "") == Uri.parse(targetUrl)) {
                    val cache = WebResourceCacheManager.providePath(this@WebViewActivity, targetUrl)
                    val mimeType = WebResourceCacheManager.getMimeTypeFromUrl(targetUrl)
                    Log.e(TAG, "use cache ${cache.length()}")
                    if (cache.exists()) {
                        val inputStream = FileInputStream(cache)
                        return WebResourceResponse(mimeType, "", inputStream)
                    }
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
        val TAG = "DNS-parser"
        withContext(Dispatchers.IO) {
            try {
                val ips = InetAddress.getByName(host)
                Log.e(TAG, "onCreate: ${ips.address}")
                Log.e(TAG, "onCreate: ${ips.hostAddress}")
                Log.e(TAG, "onCreate: ${ips.hostName}")
                Log.e(TAG, "onCreate: ${ips.isAnyLocalAddress}")
                Log.e(TAG, "onCreate: ${ips.isLinkLocalAddress}")
                Log.e(TAG, "onCreate: ${ips.isLoopbackAddress}")
            } catch (e: Exception) {
                Log.e(TAG, e.message ?: "error")
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.clearCache(true)
        Log.d(TAG, "onDestroy() called")
    }
}

class WebInterface(private val context: Context) {
    @JavascriptInterface
    fun getHtml(html: String) {
        Log.i(TAG, "html is $html")
    }
}

object WebResourceCacheManager {

    fun getMimeTypeFromUrl(url: String): String {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url)) ?: ""
    }

    fun providePath(context: Context, url: String): File {
        val dir = context.cacheDir.absolutePath + File.separator + "custom_cache"
        val name = url.hashCode().toString()
        return File(dir, name)
    }

    fun clearAll(context: Context) {
        val dir = context.cacheDir.absolutePath + File.separator + "custom_cache"
        try {
            val dirFile = File(dir)
            for (listFile in dirFile.listFiles()!!) {
                listFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun downloadResource(context: Context, url: String) {

        val mimeType = getMimeTypeFromUrl(url)

        Log.d(TAG, "downloadResource() called with: mimeType = $mimeType, url = $url ")

        val dir = context.cacheDir
        val dirPath = dir.absolutePath + File.separator + "custom_cache"
        val fileDir = File(dirPath)
        if (fileDir.exists().not()) {
            fileDir.mkdir()
        }

        val fileName = url.hashCode().toString()
        val destFile = File(dirPath, fileName)
        if (destFile.exists()) {
            Log.i(TAG, "$destFile exist ,no need download")
            return
        }

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient.Builder().build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val coding = response.header("content-encoding", "utf-8")
                Log.e(TAG, "encoding is $coding")
                val responseBody = response.body!!

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
