package com.engineer.android.mini.coroutines.old

import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

fun main() {
    log("main-0")
    runBlocking {
        getAll()
    }
    log("main-1")
}

suspend fun getAll() {
    val people = getPeople()
    log("get people $people")
    val age = getAge(people)
    log("people is $people,age is $age")
}

suspend fun getPeople(): String {
    withContext(Dispatchers.IO) {
        delay(1000)
    }
    return "mike"
}

suspend fun getAge(name: String): Int {
    withContext(Dispatchers.IO) {
        delay(1000)
    }

    return 100 + name.hashCode()
}

private fun getTime(): String {
    val time = System.currentTimeMillis()
    val format = "yyyy-MM-DD HH:mm:ss"
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(Date(time)) + ": "
}

private fun log(msg: String) {
    println("${getTime()}$msg")
}