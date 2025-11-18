package com.engineer.oxnn

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.FloatBuffer

class InferViewModel : ViewModel() {

    /* 主线程可观察的结果 */
    private val _top5 = MutableLiveData<List<Pair<String, Float>>>()
    val top5: LiveData<List<Pair<String, Float>>> = _top5
    val initSuccess = MutableLiveData(false)

    fun init(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                ResNet50V2onnx.init(context)
            }
        }
        initSuccess.value = true
    }

    /* 入口：Activity/Fragment 直接调 */
    fun predict(bitmap: Bitmap) {
        viewModelScope.launch {                // 主线程开启协程
            val result = withContext(Dispatchers.IO) {
                doInference(bitmap)            // 切到 IO 线程池
            }
            _top5.value = result               // 自动回到主线程
        }
    }

    /* 真正耗时的代码 */
    private suspend fun doInference(bitmap: Bitmap): List<Pair<String, Float>> =
        withContext(Dispatchers.IO) {
            // 1. 预处理（resize + normalize）
//            val nhwc = ResNet50V2Oxnn.bitmapToNhwc(bitmap)
            val input = ResNet50V2onnx.bitmapToChw(bitmap)
            val shape = longArrayOf(1L, 3L, 224L, 224L)
            val tensor = OnnxTensor.createTensor(
                OrtEnvironment.getEnvironment(), FloatBuffer.wrap(input), shape
            )

            // 2. 推理
            val session = ResNet50V2onnx.session   // 提前初始化好
            val inputs = mapOf("data" to tensor)
            val output = session.run(inputs)

            val raw = (output[0].value as Array<*>)[0] as FloatArray   // 长度 1000

            // 5. softmax
            val max = raw.max()
            val exp = raw.map { kotlin.math.exp(it - max) }
            val sum = exp.sum()
            val prob = exp.map { (it / sum) }.toFloatArray()


            // 3. 取 Top-5
            ResNet50V2onnx.topK(prob)
        }
}