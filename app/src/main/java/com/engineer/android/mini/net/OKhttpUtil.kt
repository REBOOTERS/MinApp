package com.engineer.android.mini.net

import okhttp3.*
import java.io.IOException

fun main() {
    OKhttpUtil.go()
}

/**
 * Created on 2021/5/2.
 * @author rookie
 */
object OKhttpUtil {
    private val TAG = "OKhttpUtil"

    val url = "https://www.baidu.com"

    val request = Request.Builder()
        .url(url).build()

    val client = OkHttpClient.Builder()
        .addInterceptor {
            println("just test intercept")
            it.proceed(it.request())
        }
        .eventListener(DefaultEventListener())
        .build()
    val call = client.newCall(request)

    fun go() {

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("onFailure() called with: call = $call, e = $e")
            }

            override fun onResponse(call: Call, response: Response) {
                println("onResponse() called with: call = $call, response = $response")
            }
        })
    }
}