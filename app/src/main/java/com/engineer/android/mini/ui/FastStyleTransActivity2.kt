package com.engineer.android.mini.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.engineer.android.mini.databinding.ActivityTansStyleBinding
import com.engineer.android.mini.ext.gone
import com.engineer.android.mini.ext.show
import com.engineer.android.mini.ml.WhiteboxCartoonGanDr
import com.engineer.android.mini.ml.WhiteboxCartoonGanFp16
import com.engineer.android.mini.ml.WhiteboxCartoonGanInt8
import com.engineer.android.mini.util.AsyncExecutor

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model


class FastStyleTransActivity2 : AppCompatActivity() {
    private val TAG = "FastStyleTransActivity_TAG"

    private val modelName = "mosaic.pt"
    private var currentBitmap: Bitmap? = null

    private lateinit var viewBinding: ActivityTansStyleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowInsetsControllerCompat(
            window, window.decorView
        ).hide(WindowInsetsCompat.Type.statusBars())
        viewBinding = ActivityTansStyleBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.pickImg.setOnClickListener {
            pickImage()
        }
        viewBinding.gen.setOnClickListener {
            refreshLoading(true)
            GlobalScope.launch {
                genImage()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    private fun showBitmap(bitmap: Bitmap) {
        viewBinding.pickResult.setImageBitmap(bitmap)

        Log.i(TAG, "ori = ${bitmap.width},${bitmap.height}")
        currentBitmap = bitmap
    }

    private fun refreshLoading(show: Boolean) {
        if (show) viewBinding.loading.show() else viewBinding.loading.gone()
    }

    private fun process(bitmap: Bitmap, cb: (Bitmap) -> Unit) {
        AsyncExecutor.fromIO().execute {
//            val b = bitmap.scale(512, 512)
            // GPU delegate
            val options = Model.Options.Builder()
                .setDevice(Model.Device.GPU)
                .setNumThreads(4)
                .build()
            val tensorImage = TensorImage.fromBitmap(bitmap)
//            val model = WhiteboxCartoonGanFp16.newInstance(this)
//            val model = WhiteboxCartoonGanDr.newInstance(this)
            val model = WhiteboxCartoonGanInt8.newInstance(this)
            val out = model.process(tensorImage)
            val result = out.cartoonizedImageAsTensorImage
            cb(result.bitmap)
            model.close()
        }
    }


    private fun genImage() {
        currentBitmap?.let {

            viewBinding.transResult.setImageBitmap(null)
            process(it) {
                runOnUiThread {
                    refreshLoading(false)
                    viewBinding.transResult.setImageBitmap(it)
                }
            }
        }
    }


    private fun pickImage() {
        // 检查设备是否支持照片选择器
        if (isPhotoPickerAvailable(this)) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            // 回退到传统方法
            checkAndRequestPermission()
        }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // 处理选择的图片
            uri?.let {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                showBitmap(bitmap)
            }
        }

    // 定义权限请求和图片选择启动器
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 权限已授予，打开相册
            openGallery()
        } else {
            // 权限被拒绝，显示提示
            Toast.makeText(this, "需要存储权限才能选择图片", Toast.LENGTH_SHORT).show()
        }
    }

    // 图片选择启动器
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        // 处理选择的图片
        uri?.let {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                showBitmap(bitmap)
                // 显示选择的图片
//                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "无法加载图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已经有权限，直接打开相册
                openGallery()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, permission
            ) -> {
                // 解释为什么需要权限
                AlertDialog.Builder(this).setTitle("需要权限")
                    .setMessage("需要存储权限才能从相册选择图片")
                    .setPositiveButton("确定") { _, _ ->
                        requestPermissionLauncher.launch(permission)
                    }.setNegativeButton("取消", null).show()
            }

            else -> {
                // 直接请求权限
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    // 打开相册选择图片
    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }
}