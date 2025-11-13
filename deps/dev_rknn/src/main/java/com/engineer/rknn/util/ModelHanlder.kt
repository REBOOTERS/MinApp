package com.engineer.rknn.util

import android.content.Context
import java.io.File
import java.io.FileOutputStream

object ModelHandler {

    fun copyModelToInternal(context: Context, destName: String, id: Int): String {
        val fileDirPath = context.cacheDir.absolutePath

        if (File(fileDirPath).exists().not()) {
            File(fileDirPath).mkdirs()
        }
        val filePath = fileDirPath + File.separator + destName
        val file = File(filePath)
        val inputStream = context.resources.openRawResource(id)
        val fileOutputStream = FileOutputStream(file)
        val buffer = ByteArray(8192)

        var count = inputStream.read(buffer)
        while (count > 0) {
            fileOutputStream.write(buffer, 0, count)
            count = inputStream.read(buffer)
        }
        fileOutputStream.close()
        inputStream.close()
        return filePath
    }


    fun modelInit(modelPath: String) {
       val result =  nativeInitYoyo(1280, 720, 3, modelPath)
    }

    private external fun nativeInitYoyo(inputW: Int, inputH: Int, inputC: Int, modelPath: String): Int

}