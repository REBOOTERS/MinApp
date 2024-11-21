package com.engineer.android.mini.ui.pure

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.screenHeight
import com.engineer.android.mini.ext.screenWidth
import com.engineer.android.mini.ext.toast
import com.engineer.compose.uitls.KeyBoardUtil


class InputFocusActivity : AppCompatActivity() {
    private val TAG = "InputFocusActivity_TAG"


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_focus)

        window.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }


        val debugTv = findViewById<TextView>(R.id.debug)
        debugTv.setOnClickListener {
            val iv = findViewById<ImageView>(R.id.debug_iv)
            val w = iv.width
            val h = iv.height
            debugTv.text = "w = $w,h=$h ,screenWidth=$screenWidth,screenHeight=$screenHeight"

            "哈哈哈".toast()
        }
        val et = findViewById<EditText>(R.id.name1)
        et.requestFocus()
        et.postDelayed({ KeyBoardUtil.showKeyboard(et) }, 200)

        val clearBtn = findViewById<View>(R.id.clear_btn)
        clearBtn.setOnClickListener {

            Toast.makeText(this, "clear btn clicked", Toast.LENGTH_SHORT).show()
        }
    }
}