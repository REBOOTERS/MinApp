package com.engineer.android.mini.net

object ReflectUtil {
    fun getFiled(any: Any, prop: String): Any? {
        var result: Any?
        val clazz = any.javaClass
        try {
            val field = clazz.getDeclaredField(prop)
            field.isAccessible = true
            result = field.get(any)
        } catch (e: Exception) {
            result = null
        }
        return result
    }
}