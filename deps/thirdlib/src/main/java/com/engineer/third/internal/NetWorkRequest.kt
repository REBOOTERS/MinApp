package com.engineer.third.internal

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.util.concurrent.TimeUnit

object NetWorkRequest {
    private val TAG = "NetWorkRequest"

    fun sendRequest() {
        Log.i(TAG, "sendRequest() called")
        val client = OkHttpClient.Builder()
            .connectTimeout(3600, TimeUnit.SECONDS)
            .readTimeout(3600, TimeUnit.SECONDS)
            .writeTimeout(3600, TimeUnit.SECONDS)
            .callTimeout(3600, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val request = Request.Builder()
            .url("http://localhost:8080")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "fail", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(TAG,"onResponse")
                val code = response.code
                val response = response.body

                Log.d(TAG,"code = $code, body = ${response.string()}")
            }

        })
    }
}