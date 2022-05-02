package com.engineer.android.mini.net.driver

import java.sql.Date as SqlDate
import java.util.Date as UtilDate


/**
 * https://mp.weixin.qq.com/s/K6sJqROCsb3ihESx70a85w
 *
 * Stack Overflow上最热门的8个Kotlin问题
 */
fun main() {
//    question1()
//    question2()
//    question3()
    question4()
    question8()
}

fun question8() {
    val date1 = UtilDate()
    println(date1)
    val date2 = SqlDate(System.currentTimeMillis())
    println(date2)

}

fun question4() {
    val sam = object :KotlinSAMInterface {
        override fun samMethod() {

        }
    }
}

fun question3() {
    val args = arrayOf("a", "b", "c", "d")
    println("size = ${args.size}")
    for (i in 0..args.size - 1) {
        print(args[i] + " ")
    }
    println("\n=====")
    for (i in 0 until args.size) {
        print(args[i] + " ")
    }
    println("\n=====")
    for (i in 0..args.lastIndex) {
        print(args[i] + " ")
    }
    println("\n=====")
    for (i in args.indices) {
        print(args[i] + " ")
    }
    println("\n=====")
    for (arg in args) {
        print(arg + " ")
    }
    println("\n=====")
    args.forEach {
        print(it + " ")
    }
    println("\n=====")
    for ((index, value) in args.withIndex()) {
        println("index = $index,value = $value")
    }
    println("=====")
    args.forEachIndexed { index, s ->
        println("index = $index ,s = $s")
    }
}

fun question2() {
    val peoples = arrayListOf(
        People(11, "mike"),
        People(31, "lily"),
        People(1, "lucy"),
        People(23, "lilei"),
        People(50, "wangqiang"),
    )

    peoples
        .filter { it.age > 30 }
        .take(3)
        .forEach {
            println(it)
        }
    println("================")
    peoples.asSequence()
        .filter { it.age < 30 }
        .toMutableList().forEach {
            println(it)
        }
}

fun question1() {
    val shuzu = intArrayOf(1, 2, 3, 4, 5)
    val listOfInt = arrayOf(1, 2, 3, 4, 5)
    println(shuzu.javaClass.name)
    println(listOfInt.javaClass.name)

    val shuzu1 = byteArrayOf(1, 2, 3, 4, 5)
    val listOfByte = arrayOf("1", "2", "3", "4", "5")
    println(shuzu1.javaClass.name)
    println(listOfByte.javaClass.name)
}


internal data class People(val age: Int, val name: String)

interface KotlinSAMInterface {
    fun samMethod()
}