package com.engineer.android.mini.better

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

fun appendStringToFile(key: String, value: String) {
    val filePath = "url.json" // 文件路径
    val file = File(filePath)

    // 检查文件是否存在，如果不存在则创建文件
    if (!file.exists()) {
        file.createNewFile()
    }

    // 使用 try-with-resources 语句自动关闭资源
    try {
        val writer = BufferedWriter(FileWriter(file.absolutePath, true)) // 打开文件进行追加写操作
        // 将字符串格式化为 JSON 格式，并追加写入文件
        writer.write("{\"$key\": \"$value\"},\n")
        writer.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

