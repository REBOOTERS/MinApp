package com.engineer.android.mini.better

import com.alibaba.fastjson.JSON
import com.engineer.android.mini.util.TimeUtil
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
    val list = arrayListOf("11", "cdd", "d", "cat")
    println(list)
    println(list.javaClass)
    val str = list.joinToString(",")
    println(str)

    val json = JSON.toJSONString(list)
    println("json is $json")
    val result: List<String> = JSON.parseArray(json, String::class.java)
    println(result)

    val aa = MaxSizeList<String>(10)
    for (i in 0..10) {
        aa.add("$i")
    }
    aa.add("a")
    println(aa)
    Collections.reverse(aa)
    println(aa)

}

class MaxSizeList<E>(private var maxSize: Int) : ArrayList<E>() {
    override fun add(element: E): Boolean {
        if (size >= maxSize) {
            removeAt(0)
        }
        return super.add(element)
    }
}

fun testCalendar() {
    //    val timeStamp = System.currentTimeMillis();
    val timeStamp = 1650245574000
    println("format date is " + TimeUtil.getTime(timeStamp))
    val date = Date(timeStamp)
    val calendar = Calendar.getInstance()
    calendar.time = date
    println(calendar.toString())
    println(calendar.get(Calendar.HOUR_OF_DAY))

    println("===============now=================")
    println(System.currentTimeMillis())
    println(TimeUtil.getTime(System.currentTimeMillis()))
    println(Calendar.getInstance().toString())
    println(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
    println(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR))
    println(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
    println(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
    println(Calendar.getInstance().get(Calendar.DAY_OF_WEEK_IN_MONTH))
    println(Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
    println("===============now=================")
}


private fun testInterval() {
    Observable.intervalRange(0L, 20.toLong(), 0, 1L, TimeUnit.SECONDS).doOnNext {
//            println(it)

        val randomValue = getRandomValue()
//            println(randomValue)
    }.doOnComplete {
        println("kkk")
    }.subscribe()

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