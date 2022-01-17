package com.engineer.android.mini.better

import io.reactivex.Observable
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * Created on 2021/8/9.
 * @author rookie
 */
fun main() {
}

private fun testInterval() {
    Observable.intervalRange(0L, 20.toLong(), 0, 1L, TimeUnit.SECONDS)
        .doOnNext {
//            println(it)

            val randomValue = getRandomValue()
//            println(randomValue)
        }
        .doOnComplete {
            println("kkk")
        }
        .subscribe()

    Thread.sleep(20000)
    println(1111)
}


private fun getRandomValue(): Int {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val min = calendar.get(Calendar.MINUTE)
    val sec = calendar.get(Calendar.SECOND)


    println("$hour:$min:$sec")

    if (hour in 23..24) {
        return 0
    } else if (hour == 6) {
        return 30
    } else if (hour == 5) {
        if (min >= 55) {
            return 30
        }
    } else if (hour == 7) {
        if (min <= 10) {
            return 30
        }
    }
    return 15
}


private fun testCake() {
    val cake = FantasyCake()
    println(cake)
    cake.addLettuce()
    cake.addPotatoShreds()
    cake.addSeaweedStrips()
    println(cake)
    println("cost = ${cake.checkout(9)}")
    println(cake)
}