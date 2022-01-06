package com.engineer.android.mini.net

import android.util.Log
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

private const val TAG = "Net"

object Net {

    private const val baseUrl = "https://www.wanandroid.com/"

    private val eventListener = object : EventListener() {
        /**
         * Invoked as soon as a call is enqueued or executed by a client. In case of thread or stream
         * limits, this call may be executed well before processing the request is able to begin.
         *
         * <p>This will be invoked only once for a single {@link Call}. Retries of different routes
         * or redirects will be handled within the boundaries of a single callStart and {@link
         * #callEnd}/{@link #callFailed} pair.
         */
        override fun callStart(call: Call?) {
            Log.e(TAG, "callStart() called with: call = $call")
        }

        /**
         * Invoked just prior to a DNS lookup. See [Dns.lookup].
         *
         *
         * This can be invoked more than 1 time for a single [Call]. For example, if the response
         * to the [Call.request] is a redirect to a different host.
         *
         *
         * If the [Call] is able to reuse an existing pooled connection, this method will not be
         * invoked. See [ConnectionPool].
         */
        override fun dnsStart(call: Call?, domainName: String?) {
            Log.e(TAG, "dnsStart() called with: call = $call, domainName = $domainName")
        }

        /**
         * Invoked immediately after a DNS lookup.
         *
         *
         * This method is invoked after [.dnsStart].
         */
        override fun dnsEnd(
            call: Call?,
            domainName: String?,
            inetAddressList: List<InetAddress?>?
        ) {
            Log.e(
                TAG,
                "dnsEnd() called with: call = $call, domainName = $domainName, " +
                        "inetAddressList = $inetAddressList"
            )
        }

        /**
         * Invoked just prior to initiating a socket connection.
         *
         *
         * This method will be invoked if no existing connection in the [ConnectionPool] can be
         * reused.
         *
         *
         * This can be invoked more than 1 time for a single [Call]. For example, if the response
         * to the [Call.request] is a redirect to a different address, or a connection is retried.
         */
        override fun connectStart(
            call: Call?,
            inetSocketAddress: InetSocketAddress?,
            proxy: Proxy?
        ) {
            Log.e(
                TAG,
                "connectStart() called with: call = $call, " +
                        "inetSocketAddress = $inetSocketAddress, proxy = $proxy"
            )
        }

        /**
         * Invoked just prior to initiating a TLS connection.
         *
         *
         * This method is invoked if the following conditions are met:
         *
         *  * The [Call.request] requires TLS.
         *  * No existing connection from the [ConnectionPool] can be reused.
         *
         *
         *
         * This can be invoked more than 1 time for a single [Call]. For example, if the response
         * to the [Call.request] is a redirect to a different address, or a connection is retried.
         */
        override fun secureConnectStart(call: Call?) {
            Log.e(TAG, "secureConnectStart() called with: call = $call")
        }

        /**
         * Invoked immediately after a TLS connection was attempted.
         *
         *
         * This method is invoked after [.secureConnectStart].
         */
        override fun secureConnectEnd(call: Call?, handshake: Handshake?) {
            Log.e(TAG, "secureConnectEnd() called with: call = $call, handshake = $handshake")
        }

        /**
         * Invoked immediately after a socket connection was attempted.
         *
         *
         * If the `call` uses HTTPS, this will be invoked after
         * [.secureConnectEnd], otherwise it will invoked after
         * [.connectStart].
         */
        override fun connectEnd(
            call: Call?, inetSocketAddress: InetSocketAddress?, proxy: Proxy?,
            protocol: Protocol?
        ) {
            Log.e(
                TAG,
                "connectEnd() called with: call = $call, " +
                        "inetSocketAddress = $inetSocketAddress," +
                        " proxy = $proxy, protocol = $protocol"
            )
        }

        /**
         * Invoked when a connection attempt fails.
         * This failure is not terminal if further routes are
         * available and failure recovery is enabled.
         *
         *
         * If the `call` uses HTTPS, this will be invoked after [.secureConnectEnd],
         * otherwise it will invoked after [.connectStart].
         */
        override fun connectFailed(
            call: Call?, inetSocketAddress: InetSocketAddress?, proxy: Proxy?,
            protocol: Protocol?, ioe: IOException?
        ) {
            Log.e(
                TAG,
                "connectFailed() called with: call = $call, " +
                        "inetSocketAddress = $inetSocketAddress, " +
                        "proxy = $proxy, protocol = $protocol, ioe = $ioe"
            )
        }

        /**
         * Invoked after a connection has been acquired for the `call`.
         *
         *
         * This can be invoked more than 1 time for a single [Call]. For example, if the response
         * to the [Call.request] is a redirect to a different address.
         */
        override fun connectionAcquired(call: Call?, connection: Connection?) {
            Log.e(
                TAG,
                "connectionAcquired() called with: call = $call, connection = $connection"
            )
        }

        /**
         * Invoked after a connection has been released for the `call`.
         *
         *
         * This method is always invoked after [.connectionAcquired].
         *
         *
         * This can be invoked more than 1 time for a single [Call]. For example, if the response
         * to the [Call.request] is a redirect to a different address.
         */
        override fun connectionReleased(call: Call?, connection: Connection?) {
            Log.e(
                TAG,
                "connectionReleased() called with: call = $call, connection = $connection"
            )
        }

        /**
         * Invoked just prior to sending request headers.
         *
         *
         * The connection is implicit, and will generally relate to the last
         * [.connectionAcquired] event.
         *
         *
         * This can be invoked more than 1 time for a single [Call]. For example, if the response
         * to the [Call.request] is a redirect to a different address.
         */
        override fun requestHeadersStart(call: Call?) {
            Log.e(TAG, "requestHeadersStart() called with: call = $call")
        }

        /**
         * Invoked immediately after sending request headers.
         *
         *
         * This method is always invoked after [.requestHeadersStart].
         *
         * @param request the request sent over the network. It is an error to access the body of this
         * request.
         */
        override fun requestHeadersEnd(call: Call?, request: Request?) {
            Log.e(TAG, "requestHeadersEnd() called with: call = $call, request = $request")
        }

        /**
         * Invoked just prior to sending a request body.
         * Will only be invoked for request allowing and
         * having a request body to send.
         *
         *
         * The connection is implicit, and will generally relate to the last
         * [.connectionAcquired] event.
         *
         *
         * This can be invoked more than 1 time for a single [Call]. For example, if the response
         * to the [Call.request] is a redirect to a different address.
         */
        override fun requestBodyStart(call: Call?) {
            Log.e(TAG, "requestBodyStart() called with: call = $call")
        }

        /**
         * Invoked immediately after sending a request body.
         *
         *
         * This method is always invoked after [.requestBodyStart].
         */
        override fun requestBodyEnd(call: Call?, byteCount: Long) {
            Log.e(TAG, "requestBodyEnd() called with: call = $call, byteCount = $byteCount")
        }

        /**
         * Invoked when a request fails to be written.
         *
         *
         * This method is invoked after [.requestHeadersStart] or [.requestBodyStart]. Note
         * that request failures do not necessarily fail the entire call.
         */
        override fun requestFailed(call: Call?, ioe: IOException?) {
            Log.e(TAG, "requestFailed() called with: call = $call, ioe = $ioe")
        }

        /**
         * Invoked just prior to receiving response headers.
         *
         *
         * The connection is implicit, and will generally relate to the last
         * [.connectionAcquired] event.
         *
         *
         * This can be invoked more than 1 time for a single [Call]. For example, if the response
         * to the [Call.request] is a redirect to a different address.
         */
        override fun responseHeadersStart(call: Call?) {
            Log.e(TAG, "responseHeadersStart() called with: call = $call")
        }

        /**
         * Invoked immediately after receiving response headers.
         *
         *
         * This method is always invoked after [.responseHeadersStart].
         *
         * @param response the response received over the network.
         * It is an error to access the body of
         * this response.
         */
        override fun responseHeadersEnd(call: Call?, response: Response?) {
            Log.e(TAG, "responseHeadersEnd() called with: call = $call, response = $response")
        }

        /**
         * Invoked just prior to receiving the response body.
         *
         *
         * The connection is implicit, and will generally relate to the last
         * [.connectionAcquired] event.
         *
         *
         * This will usually be invoked only 1 time for a single [Call],
         * exceptions are a limited set of cases including failure recovery.
         */
        override fun responseBodyStart(call: Call?) {
            Log.e(TAG, "responseBodyStart() called with: call = $call")
        }

        /**
         * Invoked immediately after receiving a response body and completing reading it.
         *
         *
         * Will only be invoked for requests having a response body e.g. won't be invoked for a
         * websocket upgrade.
         *
         *
         * This method is always invoked after [.requestBodyStart].
         */
        override fun responseBodyEnd(call: Call?, byteCount: Long) {
            Log.e(TAG, "responseBodyEnd() called with: call = $call, byteCount = $byteCount")
        }

        /**
         * Invoked when a response fails to be read.
         *
         *
         * This method is invoked after [.responseHeadersStart] or [.responseBodyStart].
         * Note that response failures do not necessarily fail the entire call.
         */
        override fun responseFailed(call: Call?, ioe: IOException?) {
            Log.e(TAG, "responseFailed() called with: call = $call, ioe = $ioe")
        }

        /**
         * Invoked immediately after a call has completely ended.  This includes delayed consumption
         * of response body by the caller.
         *
         *
         * This method is always invoked after [.callStart].
         */
        override fun callEnd(call: Call?) {
            Log.e(TAG, "callEnd() called with: call = $call")
        }

        /**
         * Invoked when a call fails permanently.
         *
         *
         * This method is always invoked after [.callStart].
         */
        override fun callFailed(call: Call?, ioe: IOException?) {
            Log.e(TAG, "callFailed() called with: call = $call, ioe = $ioe")
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .eventListener(eventListener)
        .build()

    private val okHttpClient1 = OkHttpClient()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    fun <T> createService(clazz: Class<T>): T {
        retrofit.converterFactories().forEach {
            Log.e(TAG, "converterFactories: $it")
        }
        retrofit.callAdapterFactories().forEach {
            Log.e(TAG, "callAdapterFactories: $it")
        }
        retrofit.callFactory().let {
            Log.e(TAG, "callFactory: $it")
        }
        retrofit.callbackExecutor().let {
            Log.e(TAG, "callbackExecutor: $it")
        }
        println()
        return retrofit.create(clazz)
    }

    fun <T> ccc(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }
}