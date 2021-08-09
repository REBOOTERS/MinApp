package com.engineer.android.mini.better

/**
 * Created on 2021/8/9.
 * @author rookie
 */
fun main() {
    val cake = FantasyCake()
    println(cake)
    cake.addLettuce()
    cake.addPotatoShreds()
    cake.addSeaweedStrips()
    println(cake)
    println("cost = ${cake.checkout(9)}")
    println(cake)
}