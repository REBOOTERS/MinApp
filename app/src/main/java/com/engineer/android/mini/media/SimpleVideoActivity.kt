package com.engineer.android.mini.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.engineer.android.mini.R

class SimpleVideoActivity : AppCompatActivity() {

    val path = Environment.getExternalStorageDirectory().absolutePath + "/Movies/vv.mp4"

    private lateinit var viewBinding: SimpleVideoActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_video)

    }
}