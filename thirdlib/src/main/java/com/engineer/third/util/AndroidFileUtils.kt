package com.engineer.third.util

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 *  Android 系统相关文件操作
 */
object AndroidFileUtils {
    /**
     * 读取 assets 中特定文件名称数据
     */
    fun getStringFromAssets(context: Context, filename: String): String? {
        val inputStream = context.assets.open(filename)
        var br: BufferedReader? = null
        val sb = StringBuilder()
        var line: String?
        var result: String?
        try {
            br = BufferedReader(InputStreamReader(inputStream))
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            result = sb.toString()
        } finally {
            br?.close()
        }
        return result
    }
}