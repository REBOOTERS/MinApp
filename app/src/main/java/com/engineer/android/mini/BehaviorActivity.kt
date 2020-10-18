package com.engineer.android.mini

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_behavior.*

private const val TAG = "BehaviorActivity"

class BehaviorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_behavior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            setUpStorageInfo()
        }
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.e(TAG, "onCreate: dir =$dir")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpStorageInfo() {
        val sb = StringBuilder()
        val codeCacheDir = this.codeCacheDir.absolutePath
        val cacheDir = this.cacheDir.absolutePath
        val dataDir = this.dataDir.absolutePath
        val filesDir = this.filesDir.absoluteFile
        val picDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        sb.append("codeCacheDir :$codeCacheDir").append("\n")
        sb.append("cacheDir :$cacheDir").append("\n")
        sb.append("dataDir :$dataDir").append("\n")
        sb.append("filesDir :$filesDir").append("\n")
        sb.append("picDir :$picDir").append("\n")
        storage_info.text = sb
        Log.e(TAG, "setUpStorageInfo:\n $sb")
    }
}