package com.engineer.android.mini.util


fun main() {
    val str = "abcdef中文0000zhong&be"
    println(StringUtil.len(str))
}

object StringUtil {
    private fun isASSICChar(ch: Char): Boolean {
        val chInt = ch.code
        return chInt < 128
    }

    fun len(string: String): Int {
        var count = 0
        for (i in string.indices) {
            count += if (isASSICChar(string[i])) 1 else 2
        }
        return count
    }
}