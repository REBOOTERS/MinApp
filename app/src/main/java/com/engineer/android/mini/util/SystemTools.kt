package com.engineer.android.mini.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore


/**
 * Created on 2020/10/18.
 * @author rookie
 */
object SystemTools {
    fun getImageFilePathFromUri(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme: String? = uri.scheme
        var data: String? = null
        if (scheme == null) data = uri.path else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor: Cursor? = context.contentResolver
                .query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    fun getVideoFilePathFromUri(context: Context, uri: Uri?): String? {
        if (null == uri) return null
        val scheme: String? = uri.scheme
        var data: String? = null
        if (scheme == null) data = uri.path else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor: Cursor? = context.contentResolver
                .query(uri, arrayOf(MediaStore.Video.VideoColumns.DATA), null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index: Int = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    fun getFileNameByUri(context: Context, uri: Uri): String {
        var fileName :String= System.currentTimeMillis().toString()
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            if (index >= 0) {
                fileName =
                    cursor.getString(index) ?: "error name"
            }
            cursor.close()
        }
        return fileName
    }
}