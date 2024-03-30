package com.engineer.android.mini.ext

import androidx.annotation.IntDef

object Test {
    const val A = 1
    const val B = 2

    @IntDef(A, B)
    @Retention(AnnotationRetention.SOURCE)
    annotation class AB
}


fun testAB(@Test.AB param: Int) {
    print(param)
}

fun main() {
    testAB(Test.A)
    testAB(Test.B)
}