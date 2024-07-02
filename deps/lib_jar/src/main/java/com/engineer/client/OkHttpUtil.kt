package com.engineer.client

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.platform.Platform
import okhttp3.internal.sse.RealEventSource
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Objects

fun main() {
    sseCall()
//    OkHttpUtil.asyncCall()
}

private fun sseCall() {
    val sb = StringBuilder()
    OkHttpUtil.sseHandler<SSResponse> { it, k ->
        if (it != null) {
            sb.append(it.event.data)
        }
        log(sb.toString())
        if (Objects.isNull(k).not()) {
            log(k!!)
        }
    }
}

data class SSEResult(val id: String, val data: String)
data class SSResponse(val code: Int, val message: String, val isEnd: Boolean, val event: SSEResult)

/**
 * Created on 2021/5/2.
 * @author rookie
 */
object OkHttpUtil {

    private var url = "http://localhost:8199/stream_chat"

    val request = Request.Builder().url(this.url).build()


    val client = OkHttpClient.Builder().addInterceptor {
        log("request is ${it.request().method}")
        log("request is2 ${it.request().body?.isDuplex()}")
        log("request is3 ${it.request().body?.isOneShot()}")
        val response = it.proceed(it.request())
        log("response body header \n${response.headers}")
        log("response body length ${response.body?.contentLength()}")
        response
    }.addInterceptor(NetInterceptor())
        .build()

    fun init(url: String) {
        this.url = url
    }

    inline fun <reified T> sseHandler(noinline callback: ((T?, String?) -> Unit)? = null) {
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
//                    TAG, "onEvent() called with: id = $id, type = $type, data = $data"
//                )

                val result = jsonToObj<T>(data)
                result?.let {
                    callback?.invoke(it, null)
                }
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                log(
                    TAG,
                    "onFailure() called with: eventSource = $eventSource, t = $t, response = $response"
                )
                callback?.invoke(null, t?.message ?: "")
            }

            override fun onClosed(eventSource: EventSource) {
                super.onClosed(eventSource)
                log(TAG, "onClosed() called with: eventSource = $eventSource")
                callback?.invoke(null, "closed")
            }
        }
        val eventSource = RealEventSource(request, sseListener)
        eventSource.connect(client)
    }


    fun asyncCall() {
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log("onFailure() called with: call = $call, e = $e")
                if (call.isCanceled().not()) {
                    call.cancel()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                log("onResponse() called with: call = $call, response = $response")

                response.body?.let {
                    val reader = BufferedReader(InputStreamReader(it.byteStream()))
                    var line = reader.readLine()
                    while (line != null) {
                        log(line)
                        line = reader.readLine()
                    }
                }

            }
        })
    }


    inline fun <reified T> jsonToObj(json: String): T? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(T::class.java)
        return adapter.fromJson(json)
    }
}

fun log(tag: String, msg: String) {
    if (tag != "") {
        println("$tag:$msg")
    } else {
        println(msg)
    }
//    Platform.get().log("$tag:$msg")
}

fun log(msg: String) {
    log("", msg)
}