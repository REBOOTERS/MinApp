package com.engineer.rknn.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.health.connect.datatypes.HeightRecord
import android.util.Log
import com.engineer.rknn.R
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object ModelHandler {

    fun copyModelToInternal(context: Context, destName: String, id: Int): String {
        val fileDirPath = context.cacheDir.absolutePath

        if (File(fileDirPath).exists().not()) {
            File(fileDirPath).mkdirs()
        }
        val filePath = fileDirPath + File.separator + destName
        val file = File(filePath)
        val inputStream = context.resources.openRawResource(id)
        val fileOutputStream = FileOutputStream(file)
        val buffer = ByteArray(8192)

        var count = inputStream.read(buffer)
        while (count > 0) {
            fileOutputStream.write(buffer, 0, count)
            count = inputStream.read(buffer)
        }
        fileOutputStream.close()
        inputStream.close()
        return filePath
    }


    fun modelInit(modelPath: String, type: ModelType): Int {
        val result = when (type) {
            ModelType.YOYO -> {
                nativeInitYoyo(1280, 720, 3, modelPath)
            }

            ModelType.WEWKS -> {
                nativeInitWekws(modelPath)
            }

            ModelType.RESNET -> {
                nativeInitResnet(modelPath)
            }
        }
        return result
    }

    fun bitmapToFloatArray(bmp: Bitmap): FloatArray {
        val width = bmp.width
        val height = bmp.height
        val floatValues = FloatArray(width * height * 3)

        var idx = 0
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bmp.getPixel(x, y)

                val r = (pixel shr 16 and 0xFF) / 255.0f
                val g = (pixel shr 8 and 0xFF) / 255.0f
                val b = (pixel and 0xFF) / 255.0f

                floatValues[idx++] = r
                floatValues[idx++] = g
                floatValues[idx++] = b
            }
        }
        return floatValues
    }

    fun floatArrayToByteArray(floatArray: FloatArray): ByteArray {
        val byteBuffer = ByteBuffer.allocate(floatArray.size * 4)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)  // RKNN 要求小端序
        for (f in floatArray) {
            byteBuffer.putFloat(f)
        }
        return byteBuffer.array()
    }



    fun runInfer(context: Context, type: ModelType) {
        when (type) {
            ModelType.RESNET -> {
                context.assets.open("dog_224x224.jpg").use {
                    val bitmap = BitmapFactory.decodeStream(it)
                    Log.i("RKNNActivity", "format ${bitmap.config}")
                    val bmp = if (bitmap.config == Bitmap.Config.ARGB_8888) bitmap else bitmap.copy(
                        Bitmap.Config.ARGB_8888, false
                    )
                    val input = bitmapToFloatArray(bitmap)


                    resNetInfer(floatArrayToByteArray(input), bmp.width, bmp.height, 0)
                }
            }

            ModelType.YOYO -> {

            }

            ModelType.WEWKS -> {
                infer()
            }
        }

    }


    fun release(type: ModelType) {
        when (type) {
            ModelType.YOYO -> {
                onDestroyYoyo()
            }

            ModelType.WEWKS -> {
                onDestroyWekws()
            }

            ModelType.RESNET -> {
                onDestroyResnet()
            }
        }
    }

    private external fun nativeInitYoyo(
        inputW: Int, inputH: Int, inputC: Int, modelPath: String
    ): Int

    private external fun onDestroyYoyo()

    private external fun nativeInitWekws(modelPath: String): Int
    private external fun infer()
    private external fun onDestroyWekws()

    private external fun nativeInitResnet(modelPath: String): Int
    private external fun resNetInfer(imageByte: ByteArray, w: Int, h: Int, format: Int): FloatArray
    private external fun onDestroyResnet()
}

enum class ModelType {
    YOYO, WEWKS, RESNET
}