package com.engineer.android.mini.net.driver

import com.engineer.android.mini.net.DefaultEventListener
import okhttp3.*
import java.io.IOException

fun main() {
//    OKhttpUtil.asyncCall()

    OKhttpUtil.syncCall()
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

    val customInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            return chain.proceed(chain.request())
        }
    }

    val client = OkHttpClient.Builder()
        .addNetworkInterceptor {
            println("just test network intercept")
            println("request is ${it.request()}")
            val res = it.proceed(it.request())
            println("res is ${res.body()}")
            res
        }
        .addInterceptor {
            println("just test intercept")
            println("request is ${it.request()}")
            val response = it.proceed(it.request())
            println("response is ${response.body()?.contentLength()}")
            response
        }
        .eventListener(DefaultEventListener())
        .build()
    val call: Call = client.newCall(request)

    fun asyncCall() {

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("onFailure() called with: call = $call, e = $e")
                if (call.isCanceled.not()) {
                    call.cancel()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                println("onResponse() called with: call = $call, response = $response")
                println("call.isExecuted = ${call.isExecuted}")
                if (call.isExecuted) {
                    call.cancel()
                }
            }
        })
    }

    fun syncCall() {
        runCatching {
            val response = call.execute()
            println("response == ${response.body()?.string()}")
            println("executed ？ ${call.isExecuted}")
            println("canceled ？ ${call.isCanceled}")
        }
            .onFailure { println("fail") }
            .onSuccess { println("success") }
    }
}