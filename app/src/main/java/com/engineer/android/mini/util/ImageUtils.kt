package com.engineer.android.mini.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
    private const val TAG = "ImageUtils"

    const val CORNER_TOP_LEFT = 1
    const val CORNER_TOP_RIGHT = 1 shl 1
    const val CORNER_BOTTOM_LEFT = 1 shl 2
    const val CORNER_BOTTOM_RIGHT = 1 shl 3
    const val CORNER_ALL = CORNER_TOP_LEFT or CORNER_TOP_RIGHT or CORNER_BOTTOM_LEFT or CORNER_BOTTOM_RIGHT

    /**
     * 把图片某固定角变成圆角
     *
     * @param bitmap  需要修改的图片
     * @param pixels  圆角的弧度
     * @param corners 需要显示圆弧的位置
     * @return 圆角图片
     */
    fun toRoundCorner(bitmap: Bitmap, pixels: Int, corners: Int): Bitmap {
        //创建一个等大的画布
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        //获取一个跟图片相同大小的矩形
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        //生成包含坐标的矩形对象
        val rectF = RectF(rect)
        //圆角的半径
        val roundPx = pixels.toFloat()
        paint.isAntiAlias = true //去锯齿
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        //绘制圆角矩形
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        //异或将需要变为圆角的位置的二进制变为0
        val notRoundedCorners = corners xor CORNER_ALL

        //哪个角不是圆角我再把你用矩形画出来
        if (notRoundedCorners and CORNER_TOP_LEFT != 0) {
            canvas.drawRect(0f, 0f, roundPx, roundPx, paint)
        }
        if (notRoundedCorners and CORNER_TOP_RIGHT != 0) {
            canvas.drawRect(rectF.right - roundPx, 0f, rectF.right, roundPx, paint)
        }
        if (notRoundedCorners and CORNER_BOTTOM_LEFT != 0) {
            canvas.drawRect(0f, rectF.bottom - roundPx, roundPx, rectF.bottom, paint)
        }
        if (notRoundedCorners and CORNER_BOTTOM_RIGHT != 0) {
            canvas.drawRect(rectF.right - roundPx, rectF.bottom - roundPx, rectF.right, rectF.bottom, paint)
        }
        //通过SRC_IN的模式取源图片和圆角矩形重叠部分
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        //绘制成Bitmap对象
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    fun compressImageToSize(imagePath: String?, targetSizeKB: Int): ByteArray {
        val bitmap = BitmapFactory.decodeFile(imagePath)
        bitmap?.let {
            val compressedImage = compressBitmapToByteArray(bitmap, targetSizeKB)
            bitmap.recycle() // 释放Bitmap资源
            return compressedImage
        }
        return byteArrayOf()
    }

    // 将Bitmap压缩到指定质量
    private fun compressBitmapToByteArray(originalBitmap: Bitmap, targetSizeKB: Int): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var quality = 100 // 初始质量设为100%
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

        Log.i(TAG, "quality = $quality")
        // 循环压缩直到大小小于等于目标大小
        while (byteArrayOutputStream.toByteArray().size / 1024 > targetSizeKB) {
            byteArrayOutputStream.reset() // 重置baos即清空baos
            quality -= 5 // 每次减少5%
            Log.i(TAG, "quality = $quality")
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        }

        return byteArrayOutputStream.toByteArray()
    }

    fun saveByteArrayToFile(byteArray: ByteArray, filePath: String): Boolean {
        val file = File(filePath)
        return try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(byteArray)
            fileOutputStream.flush()
            fileOutputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }


}
