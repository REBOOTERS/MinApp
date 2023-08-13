package com.engineer.android.mini.net.hilt

object InjectHolder {

    val map: HashMap<Any?, Any?> = HashMap()

    inline fun <reified T> put(t: T) {
        if (T::class.java.interfaces.size > 1) {
            throw IllegalArgumentException("${T::class.qualifiedName} implements too many interface")
        }
        for (clazz in T::class.java.interfaces) {
            map[clazz.hashCode()] = t
        }

    }

    inline fun <reified T> getResult(): T {
        return map[T::class.java.hashCode()] as T
    }

    fun printAll() {
        println(map)
    }
}

fun main() {
    InjectHolder.put(IVideoPlayerImpl())

    val player:IVideoPlayer = InjectHolder.getResult()
}