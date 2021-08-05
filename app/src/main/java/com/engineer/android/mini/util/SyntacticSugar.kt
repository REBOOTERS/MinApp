package com.engineer.android.mini.util


fun main() {
    val sugar = Sugar("mike", 21, true)

    printInfo(sugar)

    val letResult = sugar.let {
        it.happy = false
        it.name = "john"
    }
    printInfo(letResult)
}

fun printInfo(any:Any) {
    println("$any ${any.javaClass.name}")
}


data class Sugar(var name: String, var age: Int, var happy: Boolean) {
    fun <T> print(key: T) {
        when (key) {
            is Int -> {
                println("age=$age")
            }
            is String -> {
                println("name=$name")
            }
            is Boolean -> {
                println("happy=$happy")
            }
        }
    }
}