package com.engineer.android.mini.net.driver

import java.util.*


fun main() {
    mapAll()
}


fun mapAll() {
    println("main")
    val map = HashMap<String?, Int>()
    map["A"] = 1
    map["A1"] = 11
    map["A2"] = 12
    map["A3"] = 13
    map[null] = 100
    map.remove("A4")
    map["A"] = 16
    map["B"] = 19
    map[null] = 102
    println(map.size)
    println(map.values)
    println(map.keys)
    println(map.entries)
    println(map)
}