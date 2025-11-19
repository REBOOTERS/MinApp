package com.engineer.rknn

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.engineer.rknn.util.ModelHandler
import com.engineer.rknn.util.ModelType
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


private const val TAG = "RKNNActivity"

class RKNNActivity : AppCompatActivity() {

    private val currentType = ModelType.RESNET
    private var disposables: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rknn)

        var resultPath = ""
        resultPath = when (currentType) {
            ModelType.WEWKS -> {

                ModelHandler.copyModelToInternal(this, "wake.rknn", R.raw.wake)
            }

            ModelType.YOYO -> {
                ModelHandler.copyModelToInternal(this, "yolov5s.rknn", R.raw.yolov5s_rk3566)
            }

            ModelType.RESNET -> {
                ModelHandler.copyModelToInternal(this, "resnet50v2.rknn", R.raw.resnet50v2)
            }
        }

        Log.i(TAG, "model path = $resultPath")

        val result = ModelHandler.modelInit(resultPath, currentType)
        if (result == 0) {
            when (currentType) {
                ModelType.WEWKS -> {
                    disposables = Observable.interval(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.computation()).subscribe {
                            ModelHandler.runInfer(this, currentType)
                        }
                }

                ModelType.YOYO -> {}

                ModelType.RESNET -> {
                    disposables = Observable.interval(2, TimeUnit.SECONDS)
                        .map {
                            ModelHandler.runInfer(this,currentType)
                        }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            val resultTv = findViewById<TextView>(R.id.result)
                            resultTv.text = it.second
                            val inputImg = findViewById<ImageView>(R.id.input_res)
                            assets.open(it.first).use {
                                val bitmap = BitmapFactory.decodeStream(it)
                                inputImg.setImageBitmap(bitmap)
                            }
                        }

                }


            }

        }


        findViewById<TextView>(R.id.simple).text = resultPath
    }

    companion object {
        init {
            System.loadLibrary("dev_rknn")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ModelHandler.release(currentType)
        disposables?.let {
            it.dispose()
            disposables = null
        }
    }
}