package com.engineer.client

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.Pipe
import okio.buffer
import java.util.Timer
import java.util.TimerTask

class NetInterceptor : Interceptor {
    val content =
        "在Java中，Timer 类是java.util.Timer的简称，它是一个用于安排任务以后在后台线程中执行的工具。Timer可以安排一个java.util.TimerTask任务，以固定延迟或者固定周期重复执行"
    val contentArray = content.toCharArray()
    var index = 0
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jsonAdapter: JsonAdapter<SSResponse> = moshi.adapter(SSResponse::class.java)


    val timer: Timer = Timer()


    val pipe = Pipe(8192)
    val sink = pipe.sink.buffer()

    private val responseBody = object : ResponseBody() {
        override fun contentLength(): Long {
            return -1
        }

        override fun contentType(): MediaType? {
            return "text/event-stream; charset=utf-8".toMediaTypeOrNull()
        }

        override fun source(): BufferedSource {
            return pipe.source.buffer()
        }
    }

    private val task = object : TimerTask() {
        override fun run() {
            val input = contentArray[index]
            val end = index >= contentArray.size - 1
            val sseResult = SSEResult(System.currentTimeMillis().toString(), input.toString())
            val ssrResponse = SSResponse(200, "OK", end, sseResult)
            val json: String = jsonAdapter.toJson(ssrResponse)
            val mock = "data: $json\n\n"

//            println("running task on ${Thread.currentThread().name} ,with > $mock")
            sink.writeUtf8(mock)
            sink.flush()
            index++
            if (index >= contentArray.size) {
                sink.close()
                timer.cancel()
            }
        }

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        println("intercept ${request.url}")

        if (url.contains("stream_chat")) {
//            chain.proceed(request)

            timer.schedule(task, 0, 20)

            return Response.Builder().request(request).code(200).message("OK")
                .protocol(okhttp3.Protocol.HTTP_1_1).addHeader("Content-Type", "text/event-stream")
                .body(responseBody).build()
        }
        return chain.proceed(request)
    }
}