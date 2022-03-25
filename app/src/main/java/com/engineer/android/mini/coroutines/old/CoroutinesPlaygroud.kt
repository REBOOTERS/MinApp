package com.engineer.android.mini.coroutines.old

import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * coroutines:
 * https://juejin.cn/post/6987724340775108622
 */
fun main() {
//    blockWay()
//    runWay()
//    launchWay()
    asyncWay()
//    androidWay()
}

private fun androidWay() {
    log("main-0")
    GlobalScope.launch {
        log("launch-0")
        withContext(Dispatchers.IO) {
            log("withContext-0")
            delay(1000)
            log("withContext-1")
        }
        log("launch-1")
    }
    log("main-1")
    wait(6)
    log("main-2")
}

private fun asyncWay() {
    log("main-0")
    val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        log("find ex: $throwable")
    }
    GlobalScope.launch(exceptionHandler) {
        log("before async")
        val defer1 = async {
            delay(2000)
            log("in async1")
            "hello"
        }
        val defer2 = async {
            delay(2000)
            log("in async2")
            " world"
        }
        val defer3 = async(start = CoroutineStart.LAZY) {
            delay(2000)
            log("in async3")
            " again"
        }
        val defer4 = async {
            delay(2000)
            log("in async4")
            1/0
            log("in async4-1")
        }
        log("after async")
        var result = "init"
//        result = defer1.await() + defer2.await()
//        result = defer1.await() + defer2.await() + defer3.await()
        result = defer1.await() + defer2.await()
        log("result is $result")
    }
    log("main-1")
    wait(6)
    log("main-2")
}

private fun runWay() {
    log("main-0")
    val job = GlobalScope.launch {
        getAll()
    }
    log("job is $job")
    log("main-1")
    wait(4)
    log("main-2")
}

private fun launchWay() {
    log("main-0")
    val job = CoroutineScope(Dispatchers.IO).launch {
        getAll()

        launch {
            log("sub-coroutine-1")
            getAll()
        }
        launch {
            log("sub-coroutine-2")
            getAll()
        }
    }
    log("job is $job")
    log("main-1")
    wait(10)
    log("main-2")
}

private fun blockWay() {
    log("main-0")
    val t = runBlocking {
        getAll()
        100
    }
    log("t is $t")
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
    return sdf.format(Date(time))
}

private fun getThreadName() = Thread.currentThread().name

fun log(msg: String) {
    println("${getTime()} ${getThreadName()}: $msg")
}

private fun wait(second: Int) {
    Thread.sleep(second * 1000L)
}