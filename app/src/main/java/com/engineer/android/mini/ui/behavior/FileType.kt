package com.engineer.android.mini.ui.behavior

enum class FileType(val s: String) {
    GIF("image/gif"),
    WEBP("image/webp"),
    PNG("image/png"),
    JPEG("image/jpeg")
}

fun main() {
    val gif = FileType.GIF
    println(gif.name)
    println(gif.ordinal)
    println(gif.s)
    println("=================")
    val webp = FileType.WEBP
    println(webp.name)
    println(webp.ordinal)
    println(webp.s)
    println("=================")
    FileType.values().forEach {
        println(it.s)
        println(it.name)
        println(it.ordinal)
    }
}