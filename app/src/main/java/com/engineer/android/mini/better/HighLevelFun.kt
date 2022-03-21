package com.engineer.android.mini.better

import com.engineer.android.mini.coroutines.old.log


fun main() {
    asyncGet {
        log("1")
    }
    asyncGet2(1) {
        log("2")
    }

    asyncGet3(1) {
        log("3")
        1
    }

    asyncGet4(MyObj()) {
        log("4")
        this.test()
        1
    }
}

private fun asyncGet(block: () -> Unit) {
    block.invoke()
}

private fun asyncGet2(a: Int, block: () -> Unit) {
    block()
}

private fun asyncGet3(a: Int, block: (Int) -> Int): Int {
    return block(a)
}

class MyObj {
    fun test() {
        log("this is MyObj test")
    }
}

private fun asyncGet4(a: MyObj, block: MyObj.() -> Int) {
    block(a)
}