package com.engineer.android.mini.better.file

import android.os.FileObserver
import android.util.Log
import com.engineer.android.mini.MinApp
import java.io.File

object FileChangeWatcher {

    private const val TAG = "FileChangePlayground"
    private const val TARGET_FILE_DIR = "file_watcher"
    val FILE_PATH =
        MinApp.INSTANCE.filesDir.path + File.separator + TARGET_FILE_DIR

    private val fileChangeObserver = object : FileObserver(File(FILE_PATH)) {
        override fun onEvent(event: Int, path: String?) {
            Log.d(TAG, "onEvent() called with: event = $event, path = $path")
        }
    }

    fun addFile(path: String) {
        Log.d(TAG, "addFile() called with: path = $path")
        val file = FILE_PATH + File.separator + path
        val targetFile = File(file)
        if (targetFile.exists().not()) {
            targetFile.createNewFile()
        }
    }

    fun deleteFile(path: String) {
        Log.d(TAG, "deleteFile() called with: path = $path")
        val file = FILE_PATH + File.separator + path
        val targetFile = File(file)
        targetFile.deleteOnExit()
    }

    fun initObserver() {
        val targetDir = File(FILE_PATH)
        Log.d(TAG, "initObserver() called $targetDir")
        if (targetDir.exists().not()) {
            val success = targetDir.mkdir()
            Log.e(TAG, "initObserver: $success")
        }
        fileChangeObserver.startWatching()
    }

    fun stopObserve() {
        fileChangeObserver.stopWatching()
    }

}