package com.engineer.android.mini.ui.compose.pickimg

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineer.android.mini.ml.ArbitraryImageStylizationV1Tflite256Fp16TransferV1
import com.engineer.android.mini.ml.ArbitraryImageStylizationV1Tflite256Int8TransferV1
import com.engineer.android.mini.ml.PredictFloat16
import com.engineer.android.mini.ml.PredictInt8
import com.engineer.android.mini.ml.WhiteboxCartoonGanDr
import com.engineer.android.mini.ml.WhiteboxCartoonGanFp16
import com.engineer.android.mini.ml.WhiteboxCartoonGanInt8
import com.engineer.android.mini.util.AsyncExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import java.io.InputStream

class TransViewModel : ViewModel() {
    private val TAG = "TransViewModel"
    var pickedImageBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var transformedImageBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var styleBitmap by mutableStateOf<Bitmap?>(null)
        private set

    fun updatePickedImage(bitmap: Bitmap?) {
        pickedImageBitmap = bitmap
    }

    fun updateStyleImage(bitmap: Bitmap?) {
        styleBitmap = bitmap
    }

    private val _selectedOption = MutableStateFlow(0)
    val selectedOption: StateFlow<Int> = _selectedOption.asStateFlow()

    // 风格图片列表（从 Assets 加载）
    private val _styleImages = MutableStateFlow<List<ImageBitmap>>(emptyList())
    val styleImages: StateFlow<List<ImageBitmap>> = _styleImages.asStateFlow()

    fun updateSelectedOption(option: Int) {
        _selectedOption.value = option
    }

    private val _selectedStyleIndex = MutableStateFlow(-1)
    val selectedStyleIndex: StateFlow<Int> = _selectedStyleIndex.asStateFlow()

    fun updateSelectedStyle(index: Int) {
        _selectedStyleIndex.value = index
    }

    fun loadStyleImages(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val images = mutableListOf<ImageBitmap>()
            context.assets.list("thumbnails")?.forEach { i ->
                Log.d(TAG,"i == $i")
                val path = "thumbnails/${i}"
                val bitmap = loadImageFromAssets(path, context)?.asImageBitmap()
                bitmap?.let { images.add(it) }
            }
            _styleImages.value = images
            Log.d(TAG,"_size = ${_styleImages.value.size}")
        }
    }

    private fun loadImageFromAssets(path: String, context: Context): Bitmap? {
        return try {
            val inputStream: InputStream = context.assets.open(path)
            android.graphics.BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }


    fun generateImage(context: Context) {
        if (pickedImageBitmap == null) {
            return
        }
        val type = selectedOption.value
        pickedImageBitmap?.let {
            Log.d(TAG, "start gen")
            viewModelScope.launch {
                isLoading = true
                process(context, type, it) {
                    isLoading = false
                    transformedImageBitmap = it

                }
            }
        }

    }

    private fun process(context: Context, type: Int, bitmap: Bitmap, cb: (Bitmap) -> Unit) {
        AsyncExecutor.fromIO().execute {
//            val b = bitmap.scale(512, 512)
            val tensorImage = TensorImage.fromBitmap(bitmap)
            val model = getModel(context, type)

            Log.d(TAG, "use model $model")
            if (model is WhiteboxCartoonGanDr) {
                val out = model.process(tensorImage)
                val result = out.cartoonizedImageAsTensorImage
                cb(result.bitmap)
                model.close()
            }
            if (model is WhiteboxCartoonGanInt8) {
                val out = model.process(tensorImage)

                val result = out.cartoonizedImageAsTensorImage
                cb(result.bitmap)
                model.close()
            }
            if (model is WhiteboxCartoonGanFp16) {
                val out = model.process(tensorImage)

                val result = out.cartoonizedImageAsTensorImage
                cb(result.bitmap)
                model.close()
            }
            if (model is ArbitraryImageStylizationV1Tflite256Fp16TransferV1) {

                val predict = PredictFloat16.newInstance(context)
                val styleImage = TensorImage.fromBitmap(styleBitmap)
                val outputs = predict.process(styleImage)

                val styleBottleneck = outputs.styleBottleneckAsTensorBuffer
                val out = model.process(tensorImage, styleBottleneck)

                val result = out.styledImageAsTensorImage
                cb(result.bitmap)

                predict.close()
                model.close()
            }
            if (model is ArbitraryImageStylizationV1Tflite256Int8TransferV1) {
                val predict = PredictInt8.newInstance(context)

                val styleImage = TensorImage.fromBitmap(styleBitmap)

                // Runs model inference and gets result.
                val outputs = predict.process(styleImage)

                val styleBottleneck = outputs.styleBottleneckAsTensorBuffer
                Log.i(TAG, "styleBottleneck ${styleBottleneck}")


                val out = model.process(tensorImage, styleBottleneck)

                val result = out.styledImageAsTensorImage
                cb(result.bitmap)

                predict.close()
                model.close()
            }
            Log.d(TAG, "finish")

        }
    }

    private fun getModel(context: Context, type: Int): Any? {
        // GPU delegate
        val options = Model.Options.Builder().setDevice(Model.Device.GPU).setNumThreads(4).build()
        when (type) {
            0 -> {
                return WhiteboxCartoonGanDr.newInstance(context)
            }

            1 -> {
                return WhiteboxCartoonGanInt8.newInstance(context)
            }

            2 -> {
                return WhiteboxCartoonGanFp16.newInstance(context)
            }

            3 -> {
                return ArbitraryImageStylizationV1Tflite256Fp16TransferV1.newInstance(context)
            }

            4 -> {
                return ArbitraryImageStylizationV1Tflite256Int8TransferV1.newInstance(context)
            }
        }
        return null
    }
}