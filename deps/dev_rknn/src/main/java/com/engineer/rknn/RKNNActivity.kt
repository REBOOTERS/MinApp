package com.engineer.rknn

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.engineer.rknn.util.ModelHandler


private const val TAG = "RKNNActivity"

class RKNNActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rknn)

        val resultPath =
            ModelHandler.copyModelToInternal(this, "yolov5s.rknn", R.raw.yolov5s_rk3566)
        Log.i(TAG,"model path = $resultPath")
        findViewById<TextView>(R.id.simple).text = resultPath
        val result = ModelHandler.modelInit(resultPath)
    }

    companion object {
        init {
            System.loadLibrary("dev_rknn")
        }
    }
}