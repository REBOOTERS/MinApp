package com.engineer.client

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.sse.RealEventSource
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun main() {
    OkHttpUtil.sseHandler<SSResponse> {
        println(it.event)
    }

//    OkHttpUtil.asyncCall()
}

data class SSEResult(val id: String, val data: String)
data class SSResponse(val code: Int, val message: String, val event: SSEResult)

/**
 * Created on 2021/5/2.
 * @author rookie
 */
object OkHttpUtil {

    private var url = "http://localhost:8199/stream_chat"

    val request = Request.Builder().url(this.url).build()


    val client = OkHttpClient.Builder().addInterceptor {
        println("request is ${it.request()}")
        val response = it.proceed(it.request())
        println("response body header \n${response.headers}")
        println("response body length ${response.body?.contentLength()}")
        response
    }.build()

    fun init(url: String) {
        this.url = url
    }

    inline fun <reified T> sseHandler(noinline callback: ((T) -> Unit)? = null) {
        val TAG = "OkHttpUtil"
        val sseListener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                super.onOpen(eventSource, response)
                log(
                    TAG, "onOpen() called with: eventSource = $eventSource, response = $response"
                )
            }

            override fun onEvent(
                eventSource: EventSource, id: String?, type: String?, data: String
            ) {
                super.onEvent(eventSource, id, type, data)
//                log(
//                    TAG,
//                    "onEvent() called with: id = $id, type = $type, data = $data"
//                )

                val result = jsonToObj<T>(data)
                result?.let {
                    callback?.invoke(it)
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                log(
                    TAG,
                    "onFailure() called with: eventSource = $eventSource, t = $t, response = $response"
                )
            }

            override fun onClosed(eventSource: EventSource) {
                super.onClosed(eventSource)
                log(TAG, "onClosed() called with: eventSource = $eventSource")
            }
        }
        val eventSource = RealEventSource(request, sseListener)
        eventSource.connect(client)
    }


    fun asyncCall() {
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("onFailure() called with: call = $call, e = $e")
                if (call.isCanceled().not()) {
                    call.cancel()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                println("onResponse() called with: call = $call, response = $response")

                response.body?.let {
                    val reader = BufferedReader(InputStreamReader(it.byteStream()))
                    var line = reader.readLine()
                    while (line != null) {
                        println(line)
                        line = reader.readLine()
                    }
                }

            }
        })
    }


    fun log(tag: String, msg: String) {
        println("$tag:$msg")
    }

    inline fun <reified T> jsonToObj(json: String): T? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(T::class.java)
        return adapter.fromJson(json)
    }
}