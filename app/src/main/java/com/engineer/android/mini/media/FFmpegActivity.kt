package com.engineer.android.mini.media

import android.os.Bundle
import android.util.Log
import android.view.View
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityFfmpegBinding
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.common.contract.PickMp4ResultContract
import com.engineer.common.utils.AndroidFileUtils
import com.engineer.common.utils.SystemTools
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber


class FFmpegActivity : BaseActivity(), View.OnClickListener {
    private var _viewBinding: ActivityFfmpegBinding? = null
    private val viewBinding get() = _viewBinding!!

    private var myRxFFmpegSubscriber: MyRxFFmpegSubscriber? = null

    private val pickVideoLauncher = registerForActivityResult(PickMp4ResultContract()) {
        it?.let {
            Log.d(TAG, "get result() called uri  = $it")
            val path = AndroidFileUtils.getFilePathByUri(this, it)
            Log.d(TAG, "get result() called path = $path")
            path?.let {
                viewBinding.videoSelected.setVideoPath(it)
                runCommand(path)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = ActivityFfmpegBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        config()
    }

    private fun runCommand(inputPath: String) {
        val outPath = "/storage/emulated/0/Movies/result.mp4"
        val text = "ffmpeg -y -i $inputPath -vf boxblur=25:5 -preset superfast $outPath"
        val commands = text.split(" ").toTypedArray()
        myRxFFmpegSubscriber = MyRxFFmpegSubscriber(outPath)
        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(myRxFFmpegSubscriber);

    }

    inner class MyRxFFmpegSubscriber(private val outPath: String) : RxFFmpegSubscriber() {


        override fun onFinish() {
            Log.d(TAG, "onFinish() called")
            viewBinding.videoResult.setVideoPath(outPath)
        }

        override fun onProgress(progress: Int, progressTime: Long) {
            Log.d(TAG, "onProgress() called with: progress = $progress, progressTime = $progressTime")
        }

        override fun onCancel() {

        }

        override fun onError(message: String) {
            Log.d(TAG, "onError() called with: message = $message")
        }
    }

    private fun config() {
        RxFFmpegInvoke.getInstance().setDebug(true);
        viewBinding.selectVideo.setOnClickListener(this)
        viewBinding.videoSelected.setOnPreparedListener {
            it.start()
        }
        viewBinding.videoResult.setOnPreparedListener {
            it.start()
        }

    }

    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.select_video -> {
                pickVideoLauncher.launch("select mp4")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myRxFFmpegSubscriber?.dispose()
    }
}