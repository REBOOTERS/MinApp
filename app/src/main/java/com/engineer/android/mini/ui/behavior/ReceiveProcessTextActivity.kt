package com.engineer.android.mini.ui.behavior


import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R


class ReceiveProcessTextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_process_text)

        val charSequenceExtra = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
        if (charSequenceExtra != null) {
            val tvReceiveProcess = findViewById<TextView>(R.id.tv_receive_process_text)
            tvReceiveProcess.text = charSequenceExtra
        }
    }
}