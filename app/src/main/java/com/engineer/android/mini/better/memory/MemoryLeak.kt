package com.engineer.android.mini.better.memory

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * Created on 2021/5/2.
 * @author rookie
 */
fun main() {
    println("how memory leak work")
    // 引用队列
    val referenceQueue = ReferenceQueue<Pair<String, Int>?>()
    var pair: Pair<String, Int>? = Pair("mike", 24)

    // 弱引用
    val weakReference = WeakReference(pair, referenceQueue)
    println("weakReference==>" +weakReference.get())
    println("weakReferenceQueue==>" +referenceQueue.poll())

    println("not yet")

    pair = null

    System.gc()
    Thread.sleep(2000)

    println("weakReference==>" +weakReference.get())
    println("weakReferenceQueue==>" +referenceQueue.poll())

}