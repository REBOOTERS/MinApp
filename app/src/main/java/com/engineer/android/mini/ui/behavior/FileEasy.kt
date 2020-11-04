package com.engineer.android.mini.ui.behavior

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream

object FileEasy {
    suspend fun copyUriToAlbumDir(
        context: Context,
        inputUri: Uri,
        displayName: String,
        mimeType: String
    ) {
        withContext(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(inputUri)
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            } else {
                values.put(
                    MediaStore.MediaColumns.DATA,
                    "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
                )
            }
            val bis = BufferedInputStream(inputStream)
            val uri =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                val outputStream = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    val bos = BufferedOutputStream(outputStream)
                    val buffer = ByteArray(1024)
                    var bytes = bis.read(buffer)
                    while (bytes >= 0) {
                        bos.write(buffer, 0, bytes)
                        bos.flush()
                        bytes = bis.read(buffer)
                    }
                    bos.close()
                }
            }
            bis.close()
//            runOnUiThread {
//                Toast.makeText(this, "Copy file into album succeeded.", Toast.LENGTH_LONG)
//                    .show()
//            }
        }
    }
}