package com.engineer.oxnn

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider


class ONNXActivity : AppCompatActivity() {

    private lateinit var viewModel: InferViewModel

    private val TAG = "ONNXActivity_tag"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[InferViewModel::class.java]
        setContentView(R.layout.activity_onnx)

        viewModel.initSuccess.observe(this) {
            if(it) {
                Toast.makeText(this, "init success", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.init(this)

        viewModel.top5.observe(this) { topLabel ->
            topLabel.forEach {
                val fmtStr = String.format("class=%-16s  prob=%f", it.first, it.second)
                Log.i(TAG, fmtStr)
            }
        }

        findViewById<View>(R.id.infer).setOnClickListener {
            assets.open("dog_224x224.jpg").use {
                val bitmap = BitmapFactory.decodeStream(it)

                Log.i(TAG, "w = ${bitmap.width}, h = ${bitmap.height}")
                viewModel.predict(bitmap)
            }
        }
    }
}