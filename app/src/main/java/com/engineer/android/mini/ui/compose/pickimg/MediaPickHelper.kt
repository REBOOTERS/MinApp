package com.engineer.android.mini.ui.compose.pickimg

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


// 定义权限请求和图片选择启动器

object MediaPickHelper {

    fun mediaPick(context: ComponentActivity, cb: (Boolean, Bitmap?) -> Unit) {
        val pickMedia =
            context.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // 处理选择的图片
                uri?.let {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    cb(true, bitmap)
                }
            }

        if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            cb(false, null)
        }
    }

    fun checkAndRequestPermission(context: ComponentActivity, cb: (Boolean) -> Unit) {


        val requestPermissionLauncher = context.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            cb(isGranted)
        }

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                context, permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已经有权限，直接打开相册
                cb(true)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                context, permission
            ) -> {
                // 解释为什么需要权限
                AlertDialog.Builder(context).setTitle("需要权限")
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
    fun openGallery(context: ComponentActivity, cb: (Bitmap?) -> Unit) {
        val pickImageLauncher = context.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            // 处理选择的图片
            uri?.let {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    cb(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    cb(null)
                }
            }
        }

        pickImageLauncher.launch("image/*")
    }
}

