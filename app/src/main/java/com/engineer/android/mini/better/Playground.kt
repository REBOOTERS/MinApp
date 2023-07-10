package com.engineer.android.mini.better

import androidx.annotation.Keep
import com.alibaba.fastjson.JSON


@Keep
data class People(val name: String?, val age: Int?)

@Keep
data class Animal(val color: String, val code: Int)

@Keep
class Wrapper1(var list: List<People?>?)

@Keep
class Wrapper2(var list: List<Animal?>?)

@Keep
class Wrapper3(var list: List<*>?)


fun main() {
    testfastjson()
}

fun testfastjson() {
    val json = "{\"list\":[]}"
    val json2 = "{\"list\":[{\"color\":\"red\",\"code\":111},{\"color\":\"green\",\"code\":222}]}"

    val result1 = getResult(json, Wrapper1::class.java)
    val result2 = getResult(json2, Wrapper1::class.java)
    val result3 = getResult(json2, Wrapper2::class.java)
    val result4 = getResult(json2, Wrapper3::class.java)

    val condition1 = result1 is Wrapper1
    val condition2 = result2 is Wrapper1
    val condition3 = result3 is Wrapper2
    val condition4 = result4 is Wrapper3

    println("condition1 = $condition1")
    println("condition2 = $condition2")
    println("condition3 = $condition3")
    println("condition4 = $condition4")
}

fun <T : Any> getResult(json: String, clazz: Class<T>): T? {
    val result = JSON.parseObject(json, clazz)
    println("result is $result")
    return result
}