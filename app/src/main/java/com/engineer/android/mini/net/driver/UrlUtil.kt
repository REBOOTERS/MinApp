package com.engineer.android.mini.net.driver

import android.net.Uri
import java.net.InetAddress
import java.net.URLDecoder
import java.net.URLEncoder

fun main() {
    println("This is Url World !")

    val url =
        "https://search.jd.com/Search?keyword=%E5%8F%B0%E5%BC%8F%E6%9C%BA&enc=utf-8&suggest=2.his.0.0&wq=&pvid=e33792a4dc654b29af0caae2c63bce53"
    println("original ----> ")
    println(url)
    println("encode -----> ")
    val encode = URLEncoder.encode(url, "UTF-8")
    println(encode)

    println("decode -----> ")
    val decode = URLDecoder.decode(url, "UTF-8")
    println(decode)
}

fun parseUri(uri: Uri) {
    println(uri.scheme)
    println(uri.host)
    println(uri.getQueryParameter("keyword"))
}

private fun testInetAddress() {
    val address = InetAddress.getByName("www.baidu.com")
    println(address)
    println("ipVersion is " + address.address.size)
    println(address.hostAddress)
    println(address.canonicalHostName)
    println()
    val addresslist = InetAddress.getAllByName("www.google.com")
    val local = InetAddress.getLocalHost()
    addresslist.forEachIndexed { index, inetAddress ->
        println("$index , $inetAddress")
    }
    println()
    println(local)
    println(local.hostAddress)
    println(local.canonicalHostName)
}