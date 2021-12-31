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
    fun loadImages() {

        thread {
            val imageList = ArrayList<Uri>()
            val cursor = getApplication<MinApp>()
                .contentResolver
                .query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
            val cursor = getApplication<MinApp>()
                .contentResolver
                .query(
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