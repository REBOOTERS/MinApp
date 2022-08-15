package com.engineer.other

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FakePureUiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "Ha Ha Ha ,I'm Fake "
        setContentView(textView)
    }
}