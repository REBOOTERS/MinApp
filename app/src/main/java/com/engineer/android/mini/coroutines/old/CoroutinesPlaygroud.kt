package com.engineer.android.mini.coroutines.old

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun main() {
    println("main-0")
    runBlocking {
        getAll()
    }
    println("main-1")
}

suspend fun getAll() {
    val people = getPeople()
    println("get people $people")
    val age = getAge(people)
    println("people is $people,age is $age")
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