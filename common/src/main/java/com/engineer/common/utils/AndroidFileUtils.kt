package com.engineer.common.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import org.apache.commons.io.FilenameUtils
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader


/**
 *  Android 系统相关文件操作
 */
private const val TAG = "AndroidFileUtils"

object AndroidFileUtils {
    /**
     * 读取 assets 中特定文件名称数据
     */
    fun getStringFromAssets(context: Context, filename: String): String? {
        val inputStream: InputStream?
        var br: BufferedReader? = null
        val sb = StringBuilder()
        var line: String?
        val result: String?
        try {
            inputStream = context.assets.open(filename)
            br = BufferedReader(InputStreamReader(inputStream))
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            result = sb.toString()
        } catch (e: FileNotFoundException) {
            return null
        } finally {
            br?.close()
        }
        return result
    }

    fun getFilePathByUri(context: Context, uri: Uri): String? {
        var path: String? = null
        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            path = uri.path
            return path
        }
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    path = Environment.getExternalStorageDirectory().absolutePath + "/" + split[1]
                    return path
                }
            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                path = getDataColumn(context, contentUri, null, null)
                return path
            } else if (isMediaDocument(uri)) {
                // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                path = getDataColumn(context, contentUri, selection, selectionArgs)
                return path
            }
        } else if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
            val cursor: Cursor? = context.contentResolver.query(
                uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null
            )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex: Int =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex)
                    }
                }
                cursor.close()
            }
            return path
        }

        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            uri?.let {
                cursor =
                    context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                cursor?.let {
                    if (it.moveToFirst()) {
                        val columnIndex: Int = it.getColumnIndexOrThrow(column)
                        return it.getString(columnIndex)
                    }
                }
            }

        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun assembleOutputPath(inputPath: String): String {
        return FilenameUtils.getPath(inputPath) + "result_" + System.currentTimeMillis()
            .toString() + "_" + FilenameUtils.getName(inputPath)
    }
}