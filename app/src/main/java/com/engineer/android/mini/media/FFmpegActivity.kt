package com.engineer.android.mini.media

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityFfmpegBinding
import com.engineer.android.mini.media.events.ResultEvent
import com.engineer.android.mini.media.services.FFmpegService
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.common.contract.PickFileResultContract
import com.engineer.common.utils.AndroidFileUtils
import com.engineer.common.utils.RxBus
import io.microshow.rxffmpeg.RxFFmpegInvoke


class FFmpegActivity : BaseActivity(), View.OnClickListener {
    private var _viewBinding: ActivityFfmpegBinding? = null
    private val viewBinding get() = _viewBinding!!


    private val pickVideoLauncher = registerForActivityResult(PickFileResultContract("video/mp4")) {
        it?.let { uri ->
            handleResult(uri, "video")
        }
    }

    private val pickPictureLauncher = registerForActivityResult(PickFileResultContract("image/*")) {
        it?.let {
            handleResult(it, "image")
        }
    }

    private fun handleResult(uri: Uri, type: String) {
        val path = AndroidFileUtils.getFilePathByUri(this, uri)
        Log.d(TAG, "get result() called path = $path")
        path?.let { realPath ->
            val outputPath = AndroidFileUtils.assembleOutputPath(realPath)
            when (type) {
                "video" -> {
                    viewBinding.videoSelected.setVideoPath(realPath)
                }
            }

            val intent = Intent(this, FFmpegService::class.java)
            intent.putExtra("inputPath", path)
            intent.putExtra("outputPath", outputPath)
            intent.putExtra("type", type)
            startService(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = ActivityFfmpegBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        config()
    }


    private fun config() {
        RxFFmpegInvoke.getInstance().setDebug(true);
        viewBinding.selectVideo.setOnClickListener(this)
        viewBinding.selectPicture.setOnClickListener(this)
        viewBinding.videoSelected.setOnPreparedListener {
            it.start()
        }
        viewBinding.videoResult.setOnPreparedListener {
            it.start()
        }
        val d = RxBus.getInstance().toObservable(ResultEvent::class.java).subscribe {
            val outPath = it.resultPath
            viewBinding.videoResult.setVideoPath(outPath)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.select_video -> {
                pickVideoLauncher.launch("select mp4")
            }
            R.id.select_picture -> {
                pickPictureLauncher.launch("select picture")
            }
        }
    }
}