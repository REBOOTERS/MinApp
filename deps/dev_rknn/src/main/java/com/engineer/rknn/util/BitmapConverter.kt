package com.engineer.rknn.util

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import java.io.ByteArrayOutputStream

object BitmapConverter {

    /**
     * 将Bitmap转换为字节数组，使用PNG格式（无损压缩）
     * @param bitmap 要转换的Bitmap对象
     * @return 转换后的字节数组
     */
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        return bitmapToByteArray(bitmap, CompressFormat.WEBP, 100)
    }

    /**
     * 将Bitmap转换为字节数组，可指定压缩格式和质量
     * @param bitmap 要转换的Bitmap对象
     * @param format 压缩格式（PNG/JPEG/WEBP）
     * @param quality 压缩质量（0-100），对PNG格式无效
     * @return 转换后的字节数组
     */
    fun bitmapToByteArray(bitmap: Bitmap, format: CompressFormat, quality: Int): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, outputStream)
        val result = outputStream.toByteArray()
        outputStream.close()
        return result
    }

    /**
     * 将Bitmap转换为字节数组，并可选择是否回收原Bitmap
     * @param bitmap 要转换的Bitmap对象
     * @param recycle 是否在转换后回收Bitmap资源
     * @return 转换后的字节数组
     */
    fun bitmapToByteArray(bitmap: Bitmap, recycle: Boolean): ByteArray {
        val result = bitmapToByteArray(bitmap)
        if (recycle && !bitmap.isRecycled) {
            bitmap.recycle()
        }
        return result
    }

    /**
     * 从字节数组重新创建Bitmap
     * @param byteArray 包含Bitmap数据的字节数组
     * @return 重新创建的Bitmap对象
     */
    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}