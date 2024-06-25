package com.engineer.client

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.Pipe
import okio.buffer

class NetInterceptor : Interceptor {
    val datas = arrayOf(
        "data: {\"name:\":\"mike1\"}", "data: {\"name:\":\"mike2\"}", "data: {\"name:\":\"mike3\"}"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        var i = 0
        if (url.contains("stream_chat")) {
            val pipe = Pipe(8192)

            while (i < 100) {
                val sink = pipe.sink.buffer()
                sink.writeUtf8("${datas[0]}\n\n")
                sink.flush()
                Thread.sleep(500)
                i++
            }

            val responseBody = object : ResponseBody() {
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

            return Response.Builder().request(request).code(200).message("OK")
                .protocol(okhttp3.Protocol.HTTP_1_1).addHeader("Content-Type", "text/event-stream")
                .body(responseBody).build()
        }
        return chain.proceed(request)
    }
}