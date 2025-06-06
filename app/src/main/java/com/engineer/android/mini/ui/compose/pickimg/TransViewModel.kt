package com.engineer.android.mini.ui.compose.pickimg

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engineer.android.mini.ml.WhiteboxCartoonGanDr
import com.engineer.android.mini.ml.WhiteboxCartoonGanFp16
import com.engineer.android.mini.ml.WhiteboxCartoonGanInt8
import com.engineer.android.mini.util.AsyncExecutor
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model

class TransViewModel : ViewModel() {
    private val TAG = "TransViewModel"
    var pickedImageBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var transformedImageBitmap by mutableStateOf<Bitmap?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun updatePickedImage(bitmap: Bitmap?) {
        pickedImageBitmap = bitmap
    }

    fun generateImage(context: Context, type: Int) {
        if (pickedImageBitmap == null) {
            return
        }
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


        }
    }

    private fun getModel(context: Context, type: Int): Any? {
        // GPU delegate
        val options = Model.Options.Builder().setDevice(Model.Device.GPU).setNumThreads(4).build()
        when (type) {
            0 -> {
                return WhiteboxCartoonGanDr.newInstance(context)
            }

            8 -> {
                return WhiteboxCartoonGanInt8.newInstance(context)
            }

            16 -> {
                return WhiteboxCartoonGanFp16.newInstance(context)
            }
        }
        return null
    }
}