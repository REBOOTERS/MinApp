package com.engineer.android.mini.media

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityFfmpegBinding
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.media.events.ResultEvent
import com.engineer.android.mini.media.services.FFmpegService
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.common.contract.PickFileResultContract
import com.engineer.common.contract.PickMp4ResultContract
import com.engineer.common.utils.AndroidFileUtils
import com.engineer.common.utils.RxBus
import io.microshow.rxffmpeg.RxFFmpegInvoke


class FFmpegActivity : BaseActivity(), View.OnClickListener {
    private var _viewBinding: ActivityFfmpegBinding? = null
    private val viewBinding get() = _viewBinding!!


    private val pickVideoLauncher = registerForActivityResult(PickMp4ResultContract()) {
        it?.let {
            Log.d(TAG, "get result() called uri  = $it")
            val path = AndroidFileUtils.getFilePathByUri(this, it)
            Log.d(TAG, "get result() called path = $path")
            path?.let {
                viewBinding.videoSelected.setVideoPath(it)
                val intent = Intent(this, FFmpegService::class.java)
                intent.putExtra("inputPath", path)
                startService(intent)
            }
        }
    }

    private val pickPictureLauncher = registerForActivityResult(PickFileResultContract()) {
        it?.let {
            val path = AndroidFileUtils.getFilePathByUri(this, it)
            path.toast()
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