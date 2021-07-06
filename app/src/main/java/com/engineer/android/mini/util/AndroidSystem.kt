package com.engineer.android.mini.util

import java.lang.Exception

object AndroidSystem {

    @Volatile
    private var sIsHarmonyOS: Boolean? = null

    fun isHarmonyOS(): Boolean {
        if (sIsHarmonyOS == null) {
            synchronized(AndroidSystem::class.java) {
                sIsHarmonyOS = try {
                    val clz = Class.forName("com.huawei.system.BuildEx")
                    val method = clz.getMethod("getOsBrand")
                    "harmony" == method.invoke(clz)
                } catch (e: Exception) {
                    false
                }
            }
        }
        return sIsHarmonyOS!!
    }
}