package com.engineer.oxnn

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ONNXActivity : AppCompatActivity() {

    private lateinit var viewModel: InferViewModel
    private val TAG = "ONNXActivity_tag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[InferViewModel::class.java]
        setContentView(R.layout.activity_onnx)

        viewModel.initSuccess.observe(this) {
            if (it) {
                Toast.makeText(this, "init success", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.init(this)
        val resultTv = findViewById<TextView>(R.id.simple)
        viewModel.top5.observe(this) { topLabel ->
            val sb = StringBuilder()
            topLabel.forEach {
                val fmtStr = String.format("class=%-46s  prob=%f", it.first, it.second)
                Log.i(TAG, fmtStr)
                sb.append(fmtStr).append("\n")

            }
            resultTv.text = sb.toString()
        }

        findViewById<View>(R.id.infer).setOnClickListener {
            assets.open("dog_224x224.jpg").use {
                val bitmap = BitmapFactory.decodeStream(it)
                infer(bitmap)
            }
        }
        findViewById<View>(R.id.select).setOnClickListener {
            selectAndInfer()
        }
    }

    private fun infer(bitmap: Bitmap) {
        Log.i(TAG, "w = ${bitmap.width}, h = ${bitmap.height}")
        viewModel.predict(bitmap)
    }

    /**
     * 1. 从系统相册选择图片，返回结果后执行推理
     * 2. 从相机拍照后执行推理
     */
    private fun selectAndInfer() {
        try {
            AlertDialog.Builder(this).setTitle("选择图片来源")
                .setItems(arrayOf("相册", "拍照")) { _, which ->
                    when (which) {
                        0 -> {
                            try {
                                galleryLauncher.launch("image/*")
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(
                                    this, "未找到支持图片选择的应用程序", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        1 -> {
                            cameraImageUri = createTempImageFile()
                            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                try {
                                    cameraLauncher.launch(cameraImageUri)
                                } catch (e: ActivityNotFoundException) {
                                    Toast.makeText(this, "未找到相机应用程序", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } else {
                                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
                            }
                        }
                    }
                }.show()
        } catch (e: Exception) {
            Toast.makeText(this, "选择图片时出错: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 相册选择Launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleImageUri(it) }
    }

    // 拍照Launcher
    private lateinit var cameraImageUri: Uri
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            handleImageUri(cameraImageUri)
        }
    }

    private fun handleImageUri(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                infer(bitmap)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "图片加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createTempImageFile(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        )
        return FileProvider.getUriForFile(
            this, "${applicationContext.packageName}.fileprovider", file
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraImageUri = createTempImageFile()
            cameraLauncher.launch(cameraImageUri)
        }
    }
}