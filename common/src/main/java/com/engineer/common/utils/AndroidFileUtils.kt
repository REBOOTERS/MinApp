package com.engineer.common.utils

import android.content.Context
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader

/**
 *  Android 系统相关文件操作
 */
private const val TAG = "AndroidFileUtils"

object AndroidFileUtils {
    /**
     * 读取 assets 中特定文件名称数据
     */
    fun getStringFromAssets(context: Context, filename: String): String? {
        val inputStream: InputStream?
        var br: BufferedReader? = null
        val sb = StringBuilder()
        var line: String?
        val result: String?
        try {
            inputStream = context.assets.open(filename)
            br = BufferedReader(InputStreamReader(inputStream))
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            result = sb.toString()
        } catch (e: FileNotFoundException) {
            return null
        } finally {
            br?.close()
        }
        return result
    }
}