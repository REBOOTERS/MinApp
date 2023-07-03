package com.engineer.android.mini.net

import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * Created on 2021/5/2.
 * @author rookie
 */
open class DefaultEventListener : EventListener() {

    /**
     * Invoked as soon as a call is enqueued or executed by a client. In case of thread or stream
     * limits, this call may be executed well before processing the request is able to begin.
     *
     *
     * This will be invoked only once for a single [Call]. Retries of different routes
     * or redirects will be handled within the boundaries
     * of a single callStart and [ ][.callEnd]/[.callFailed] pair.
     */
    override fun callStart(call: Call) {
        println("callStart() called with: call = $call")
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
    override fun dnsStart(call: Call, domainName: String) {}

    /**
     * Invoked immediately after a DNS lookup.
     *
     *
     * This method is invoked after [.dnsStart].
     */
    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<@JvmSuppressWildcards InetAddress>) {}

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
    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {}

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
    override fun secureConnectStart(call: Call) {}

    /**
     * Invoked immediately after a TLS connection was attempted.
     *
     *
     * This method is invoked after [.secureConnectStart].
     */
    override fun secureConnectEnd(call: Call, handshake: Handshake?) {}

    /**
     * Invoked immediately after a socket connection was attempted.
     *
     *
     * If the `call` uses HTTPS, this will be invoked after
     * [.secureConnectEnd], otherwise it will invoked after
     * [.connectStart].
     */
    override fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
    }

    /**
     * Invoked when a connection attempt fails. This failure is not terminal if further routes are
     * available and failure recovery is enabled.
     *
     *
     * If the `call` uses HTTPS, this will be invoked after [.secureConnectEnd],
     * otherwise it will invoked after [.connectStart].
     */
    override fun connectFailed(
        call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?, ioe: IOException
    ) {
    }

    /**
     * Invoked after a connection has been acquired for the `call`.
     *
     *
     * This can be invoked more than 1 time for a single [Call]. For example, if the response
     * to the [Call.request] is a redirect to a different address.
     */
    override fun connectionAcquired(call: Call, connection: Connection) {}

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
    override fun connectionReleased(call: Call, connection: Connection) {}

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
    override fun requestHeadersStart(call: Call) {}

    /**
     * Invoked immediately after sending request headers.
     *
     *
     * This method is always invoked after [.requestHeadersStart].
     *
     * @param request the request sent over the network. It is an error to access the body of this
     * request.
     */
    override fun requestHeadersEnd(call: Call, request: Request) {}

    /**
     * Invoked just prior to sending a request body.  Will only be invoked for request allowing and
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
    override fun requestBodyStart(call: Call) {}

    /**
     * Invoked immediately after sending a request body.
     *
     *
     * This method is always invoked after [.requestBodyStart].
     */
    override fun requestBodyEnd(call: Call, byteCount: Long) {}

    /**
     * Invoked when a request fails to be written.
     *
     *
     * This method is invoked after [.requestHeadersStart] or [.requestBodyStart]. Note
     * that request failures do not necessarily fail the entire call.
     */
    override fun requestFailed(call: Call, ioe: IOException) {}

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
    override fun responseHeadersStart(call: Call) {}

    /**
     * Invoked immediately after receiving response headers.
     *
     *
     * This method is always invoked after [.responseHeadersStart].
     *
     * @param response the response received over the network. It is an error to access the body of
     * this response.
     */
    override fun responseHeadersEnd(call: Call, response: Response) {}

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
    override fun responseBodyStart(call: Call) {}

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
    override fun responseBodyEnd(call: Call, byteCount: Long) {}

    /**
     * Invoked when a response fails to be read.
     *
     *
     * This method is invoked after [.responseHeadersStart] or [.responseBodyStart].
     * Note that response failures do not necessarily fail the entire call.
     */
    override fun responseFailed(call: Call, ioe: IOException) {}

    /**
     * Invoked immediately after a call has completely ended.  This includes delayed consumption
     * of response body by the caller.
     *
     *
     * This method is always invoked after [.callStart].
     */
    override fun callEnd(call: Call) {
        println("callEnd() called with: call = $call")
    }

    /**
     * Invoked when a call fails permanently.
     *
     *
     * This method is always invoked after [.callStart].
     */
    override fun callFailed(call: Call, ioe: IOException) {}

}