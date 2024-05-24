package com.engineer.android.mini.better.regex


import java.util.Locale

fun main() {
    println("hello ")

    val text = "Please contact us at support@example.com or sales@example.com."
    val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[a-z]{2,6}")

    println(emailRegex.matches(text))

    val matchResult = emailRegex.find(text)
    println("matchResult ${matchResult?.value}")
    println("group ${matchResult?.groups}")
    println(matchResult?.groups?.get(0))
//    println(matchResult?.groups?.get(1)?.value)
//    println(matchResult?.groups?.get(2)?.value)
//    println(matchResult?.groups?.get(3)?.value)


    val matchAllResult = emailRegex.findAll(text)
    println("matchAllResult size = $matchAllResult")
    println()
    matchAllResult.forEach {
        println(it.value)
        println(it.range)
        println(it.groups)
        println(it.groupValues)

        println("=========================")
    }

    val newResult = emailRegex.replace(text, "O(∩_∩)O哈哈~")
    println(newResult)
    val newnewResult = emailRegex.replace(text) { matchResult ->
        val sb = StringBuilder()
        sb.append(matchResult.value.uppercase(Locale.getDefault()))
        sb
    }
    println(newnewResult)
    val result1 = emailRegex.replaceFirst(text,"_(:з」∠)_ogo")
    println(result1)
}