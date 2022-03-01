package com.engineer.android.mini.better.interfaces

interface FooInterface {
    fun a()
    fun b()
}

interface BarInterface {
    fun m()
    fun n()
}

interface FooBar : FooInterface, BarInterface {

}

class FooBarImpl : FooBar {
    override fun a() {
    }

    override fun b() {
    }

    override fun m() {
    }

    override fun n() {
    }
}

fun main() {
    val foo = FooBarImpl()
    val bar = FooBarImpl()

    foo.a()
    foo.b()
    bar.a()
    bar.m()
}