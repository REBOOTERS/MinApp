package com.engineer.android.mini.ui.viewmodel

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.engineer.android.mini.MinApp
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.concurrent.thread

/**
 * Created on 2020/10/18.
 * @author rookie
 */
@HiltViewModel
class CursorQueryViewModel @Inject constructor(app: Application) : AndroidViewModel(app) {
    internal val imageResults = MutableLiveData<List<Uri>>()
    internal val videoResults = MutableLiveData<List<Uri>>()

    fun loadGifs() {
        val sectionHolder = buildSelectionAndArgs(listOf("image/gif"))
        loadImageInternal(sectionHolder.first, sectionHolder.second)
    }

    fun loadNoAnimImages() {
        val sectionHolder =
            buildSelectionAndArgs(listOf("image/png", "image/jpeg", "image/x-ms-bmp","image/webp"))
        loadImageInternal(sectionHolder.first, sectionHolder.second)

    }

    private fun buildSelectionAndArgs(mimeTypes: List<String>): Pair<String, Array<String>> {
        val selection: String
        val selectionArgs: Array<String>

        if (mimeTypes.isEmpty()) {
            // 如果没有指定 MIME 类型，则返回一个始终为真的条件（例如大小大于0）
            selection = MediaStore.MediaColumns.SIZE + " > 0"
            selectionArgs = arrayOf()
        } else {
            // 构建 MIME 类型的条件
            val placeholders = mimeTypes.joinToString(", ") { "?" }
            selection =
                MediaStore.Images.Media.MIME_TYPE + " IN ($placeholders)" + " AND " + MediaStore.MediaColumns.SIZE + " > 0"
            selectionArgs = mimeTypes.toTypedArray()
        }

        return Pair(selection, selectionArgs)
    }

    fun loadImages() {
        loadNoAnimImages()
//        loadImageInternal(null, null)
    }

    private fun loadImageInternal(selection: String?, selectionArgs: Array<String>?) {
        thread {
            val imageList = ArrayList<Uri>()
            val cursor = getApplication<MinApp>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                selection,
                selectionArgs,
                "${MediaStore.MediaColumns.DATE_ADDED} desc"
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageList.add(uri)
                }
                cursor.close()
            }
            imageResults.postValue(imageList)
        }
    }

    fun loadVideos() {
        thread {
            val imageList = ArrayList<Uri>()
            val cursor = getApplication<MinApp>().contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                "${MediaStore.MediaColumns.DATE_ADDED} desc"
            )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    imageList.add(uri)
                }
                cursor.close()
            }
            videoResults.postValue(imageList)
        }
    }
}