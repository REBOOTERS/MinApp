package com.engineer.compose.ui.util

import android.annotation.SuppressLint
import android.app.Activity
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * https://www.zhihu.com/question/41840316/answer/2425976397
 */
object AudioRecordHelper {

    private const val source = MediaRecorder.AudioSource.DEFAULT
    private const val sample = 16000
    private const val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private const val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private const val bufferSize = 2038
    private var audioRecord: AudioRecord? = null
    private var buffer: ByteArray = ByteArray(bufferSize)

    private lateinit var audioFileName: String


    private var isRecorded = false
    private var recordingThread: Thread? = null


    @SuppressLint("MissingPermission")
    fun init(fragment: Activity) {
        audioFileName = fragment.baseContext.cacheDir.absolutePath + File.separator + "myaudio.pcm"
        audioRecord = AudioRecord(source, sample, channelConfig, audioFormat, bufferSize)
    }

    fun startRecord() {
        if (!isRecorded) {

            buffer = ByteArray(bufferSize)
            audioRecord!!.startRecording()
            isRecorded = true
            recordingThread = Thread(::startRecordingWorker)
            recordingThread?.start()
        }
    }

    fun stopRecording() {
        if (isRecorded) {
            audioRecord!!.stop()
            audioRecord!!.release()
            audioRecord = null
            isRecorded = false
        }
    }


    private fun startRecordingWorker() {
        var readSize: Int
        val file: File = File(audioFileName)
        try {
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

}