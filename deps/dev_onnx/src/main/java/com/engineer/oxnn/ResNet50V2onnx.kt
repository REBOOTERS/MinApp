package com.engineer.oxnn

import ai.onnxruntime.*
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ResNet50V2onnx {
    // 模型地址：https://s3.amazonaws.com/onnx-model-zoo/resnet/resnet50v2/resnet50v2.onnx
    private const val modelName = "resnet50v2.onnx"
    private val TAG = "ResNet50V2Oxnn"

    private val labels = mutableListOf<String>()

    // 加载模型，仅做一次
    private val ortEnv: OrtEnvironment = OrtEnvironment.getEnvironment()
    internal lateinit var session: OrtSession

    fun init(context: Context): Boolean {
        copyModelToInternal(context)
        val modelPath = copyModelToInternal(context)?.absolutePath
        modelPath?.let { s ->
            session = ortEnv.createSession(s, OrtSession.SessionOptions())

            session.inputNames.forEach { name ->

                val info = session.inputInfo[name]!!.info as TensorInfo
                Log.d(TAG, "input $name  shape=${info.shape.contentToString()}")

            }
            session.outputNames.forEach { name ->
                val info = session.outputInfo[name]!!.info as TensorInfo
                Log.d(TAG, "output $name  shape=${info.shape.contentToString()}")
            }

        }
        load(context)
        return true

    }

    private fun copyModelToInternal(context: Context): File? {
        val modelFile = File(context.filesDir, modelName)
        if (modelFile.exists()) {                 // 只拷一次
            return modelFile
        }

        return try {
            context.assets.open(modelName).use { ins ->
                FileOutputStream(modelFile).use { outs ->
                    ins.copyTo(outs)                  // 默认 8 KB 缓冲
                }
            }
            modelFile                                 // 成功
        } catch (e: IOException) {                  // 文件不存在或其它 IO 错误
            Log.e(TAG, "Fail to copy $modelName", e)
            null
        }
    }

    /* 仅在 Application 或 Activity 初始化时调用一次 */
    private fun load(context: Context) {
        context.assets.open("labels.txt").bufferedReader().useLines { lines ->
            lines.forEach { line ->
                // 0:tench, Tinca tinca  -> 取 ":" 后字符串
                labels += line.substringAfter(":").trim()
            }
        }
    }

    /* 输入：predict() 得到的 1000 维概率；输出：Top-5 类别+概率 */
    fun topK(prob: FloatArray, k: Int = 5): List<Pair<String, Float>> {
        return prob.withIndex().sortedByDescending { it.value }.take(k)
            .map { labels[it.index] to it.value }
    }


    /**
     * ARGB Bitmap -> NHWC 顺序 FloatArray，已做 (x - mean) / std
     * 返回数组长度 1*H*W*3，可直接用于 Ort 输入
     */
    fun bitmapToNhwc(
        bmp: Bitmap,
        mean: FloatArray = floatArrayOf(123.675f, 116.28f, 103.53f),
        std: FloatArray = floatArrayOf(58.82f, 58.82f, 58.82f)
    ): FloatArray {
        val w = 224
        val h = 224
        val pixels = IntArray(w * h)
        val resized = Bitmap.createScaledBitmap(bmp, w, h, true)
        resized.getPixels(pixels, 0, w, 0, 0, w, h)

        val nhwc = FloatArray(1 * h * w * 3)
        var idx = 0
        for (px in pixels) {
            val r = ((px shr 16) and 0xFF).toFloat()
            val g = ((px shr 8) and 0xFF).toFloat()
            val b = (px and 0xFF).toFloat()

            nhwc[idx++] = (r - mean[0]) / std[0]
            nhwc[idx++] = (g - mean[1]) / std[1]
            nhwc[idx++] = (b - mean[2]) / std[2]
        }
        return nhwc
    }

    /**
     * ARGB Bitmap -> CHW 顺序 FloatArray，已做 (x - mean) / std
     * 返回数组长度 3*H*W，可直接用于 Ort 输入
     */
    fun bitmapToChw(
        bmp: Bitmap,
        mean: FloatArray = floatArrayOf(0.485f, 0.456f, 0.406f),
        std: FloatArray = floatArrayOf(0.229f, 0.224f, 0.225f)
    ): FloatArray {
        val w = 224
        val h = 224
        val resized = Bitmap.createScaledBitmap(bmp, w, h, true)
        val pixels = IntArray(w * h)
        resized.getPixels(pixels, 0, w, 0, 0, w, h)
        // Proper CHW ordering: channel planes contiguous (R plane, G plane, B plane)
        val hw = h * w
        val chw = FloatArray(3 * hw)
        for (pi in pixels.indices) {
            val px = pixels[pi]
            val r = ((px shr 16) and 0xFF).toFloat() / 255f
            val g = ((px shr 8) and 0xFF).toFloat() / 255f
            val b = (px and 0xFF).toFloat() / 255f

            val rn = (r - mean[0]) / std[0]
            val gn = (g - mean[1]) / std[1]
            val bn = (b - mean[2]) / std[2]

            chw[0 * hw + pi] = rn
            chw[1 * hw + pi] = gn
            chw[2 * hw + pi] = bn
        }
        return chw
    }
}