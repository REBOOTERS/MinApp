package com.engineer.android.mini.media.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.media.events.ResultEvent
import com.engineer.common.notification.NotificationHelper
import com.engineer.common.utils.RxBus
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber

class FFmpegService : Service() {
    private val TAG = "FFmpegService"

    private var myRxFFmpegSubscriber: MyRxFFmpegSubscriber = MyRxFFmpegSubscriber()
    private var outputPath: String = ""

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            val inputPath = intent.getStringExtra("inputPath")
            val outputPath = intent.getStringExtra("outputPath")
            val type = intent.getStringExtra("type")
            val notification = NotificationHelper.provideForegroundNotification(this, "progress")
            startForeground(NotificationHelper.provideNotificationId(), notification)
            runCommand(inputPath, outputPath, type)
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun runCommand(inputPath: String?, outputPath: String?, type: String?) {
        if (TextUtils.isEmpty(inputPath) || TextUtils.isEmpty(outputPath)) {
            return
        }
        this.outputPath = outputPath!!
        when (type) {
            "video" -> {
                val text = "ffmpeg -y -i $inputPath -vf boxblur=25:5 -preset superfast $outputPath"
                val commands = text.split(" ").toTypedArray()
                RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(myRxFFmpegSubscriber)
            }
            "image" -> {
                inputPath.toast()
            }
        }


    }

    inner class MyRxFFmpegSubscriber() : RxFFmpegSubscriber() {


        override fun onFinish() {
            Log.d(TAG, "onFinish() called")

            RxBus.getInstance().post(ResultEvent(outputPath))
            stopSelf()
        }

        override fun onProgress(progress: Int, progressTime: Long) {
            Log.d(TAG, "onProgress() called with: progress = $progress, progressTime = $progressTime")
            NotificationHelper.updateProgress(baseContext, progress)
        }

        override fun onCancel() {

        }

        override fun onError(message: String) {
            Log.d(TAG, "onError() called with: message = $message")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        myRxFFmpegSubscriber.dispose()
    }
}