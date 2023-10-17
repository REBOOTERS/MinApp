package com.engineer.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * https://www.zhihu.com/question/41840316/answer/2425976397
 */
object AudioRecordHelper {


    private const val TAG = "AudioRecordHelper"

    private const val source = MediaRecorder.AudioSource.DEFAULT
    const val sample = 16000
    const val channelConfig = AudioFormat.CHANNEL_IN_MONO
    const val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    const val bufferSize = 2048

    private var audioRecord: AudioRecord? = null
    private var buffer: ByteArray = ByteArray(bufferSize)

    private lateinit var audioFileName: String


    private var isRecorded = false
    private var recordingThread: Thread? = null
    private var callback: Callback? = null
    private val sb = StringBuilder()


    @SuppressLint("StaticFieldLeak")
    fun init(fragment: Activity) {
        Log.i(TAG, fragment.baseContext.filesDir.absolutePath)
        Log.i(TAG, fragment.baseContext.cacheDir.absolutePath)
        Log.i(TAG, fragment.baseContext.packageCodePath)
        Log.i(TAG, fragment.baseContext.getExternalFilesDirs(Environment.DIRECTORY_DCIM)[0].absolutePath)
    }

    /**
     * 开始录制
     */
    @SuppressLint("MissingPermission")
    fun startRecord(context: Context) {
        if (!isRecorded) {
            // 使用应用私有目录，不用申请 sd 卡权限
            audioFileName =
                context?.cacheDir?.absolutePath + File.separator + "${System.currentTimeMillis()}_myaudio" + ".pcm"
            Log.i(TAG, audioFileName)
            if (audioRecord == null) {
                audioRecord = AudioRecord(source, sample, channelConfig, audioFormat, bufferSize)
            }
            buffer = ByteArray(bufferSize)
            audioRecord!!.startRecording()
            isRecorded = true
            recordingThread = Thread(::startRecordingWorker)
            recordingThread?.start()

            Thread(::progressWorker).start()
        }
    }

    /**
     *
     * 停止录制
     */
    fun stopRecording() {
        Log.d(TAG, "before stopRecording isRecorded = $isRecorded")
        if (isRecorded) {
            audioRecord!!.stop()
            audioRecord!!.release()
            audioRecord = null
            isRecorded = false

            Log.e(TAG, "stopRecording")
            sb.clear()
            callback?.progress(sb.toString())
        }
    }

    /**
     * 执行具体录制工作的线程，一边录制一边将数据写入 audioFileName 指定的文件
     */
    private fun startRecordingWorker() {
        var readSize: Int
        val file = File(audioFileName)
        if (file.exists()) {
            file.delete()
        }
        try {
            Log.e(TAG, "startRecordingWorker")
            while (isRecorded) {
                readSize = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                if (readSize > 0) {
                    val fos = FileOutputStream(file, true)
                    fos.write(buffer, 0, readSize)
                    fos.flush()
                    fos.close()
                } else {
                    // Error handling code goes here.
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            stopRecording()
        }
    }

    fun getPcmPath(): String {
        return audioFileName
    }


    /**
     * 录制过程回调线程，确保录制进行时在 ui 侧有感知
     */
    private fun progressWorker() {
        sb.clear()
        while (isRecorded) {
            Thread.sleep(300)
            sb.append(".")
            if (isRecorded) {
                callback?.progress(sb.toString())
            }
        }
    }

    fun setCallback(cb: Callback) {
        callback = cb
    }

    interface Callback {
        fun progress(value: String)
    }

}