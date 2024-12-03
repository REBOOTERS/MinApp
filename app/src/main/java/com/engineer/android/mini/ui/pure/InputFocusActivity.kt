package com.engineer.android.mini.ui.pure

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityInputFocusBinding
import com.engineer.android.mini.ext.screenHeight
import com.engineer.android.mini.ext.screenWidth
import com.engineer.android.mini.ext.toast
import com.engineer.compose.uitls.KeyBoardUtil


class InputFocusActivity : AppCompatActivity() {
    private val TAG = "InputFocusActivity_TAG"

    private lateinit var viewBinding: ActivityInputFocusBinding

    private var edit1Focus = false
    private var edit2Focus = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInputFocusBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        window.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        viewBinding.clearBtn.setOnClickListener {
            Toast.makeText(this, "clear btn clicked", Toast.LENGTH_SHORT).show()
            viewBinding.clearBtn.text = System.currentTimeMillis().toString()
        }

        viewBinding.debug.setOnClickListener {
            val iv = findViewById<ImageView>(R.id.debug_iv)
            val w = iv.width
            val h = iv.height
            viewBinding.debug.text =
                "w = $w,h=$h ,screenWidth=$screenWidth,screenHeight=$screenHeight"

            "哈哈哈".toast()
        }

        viewBinding.name1.requestFocus()
        viewBinding.name1.postDelayed({ KeyBoardUtil.showKeyboard(viewBinding.name1) }, 700)

//        viewBinding.clearBtn.requestFocus()

        viewBinding.name2.setOnFocusChangeListener { v, hasFocus ->
            edit2Focus = hasFocus
            handleKeyboardWithFocus()
            if (hasFocus.not()) {
                viewBinding.name2.isFocusable = false
                val kk = viewBinding.clearBtn.requestFocus()
                Log.e(TAG,"name2 lost focus ,clean btn requestFocus $kk")
            }
        }
        viewBinding.name1.setOnFocusChangeListener { v, hasFocus ->
            edit1Focus = hasFocus
            if (edit1Focus) {
                viewBinding.name2.isFocusable = true
            }
            handleKeyboardWithFocus()
        }

//        viewBinding.name1.isEnabled = false
        viewBinding.name2.isEnabled = true
        viewBinding.ddd.setOnClickListener {
            Toast.makeText(this, "hhhhhh", Toast.LENGTH_SHORT).show()
            viewBinding.ddd.text = System.currentTimeMillis().toString()
        }
    }


    private fun handleKeyboardWithFocus() {
        Log.d(
            TAG,
            "handleKeyboardWithFocus() called edit1Focus = $edit1Focus,edit2Focus = $edit2Focus"
        )
        if (edit1Focus || edit2Focus) {
            if (edit1Focus) {
                KeyBoardUtil.showKeyboard(viewBinding.name1)
            } else {
                KeyBoardUtil.showKeyboard(viewBinding.name2)
            }
        } else {
            KeyBoardUtil.hideKeyboard(viewBinding.root)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(TAG, "onKeyDown() called with: keyCode = $keyCode, event = $event")
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            val currentFocusedView = currentFocus
            val next = currentFocusedView?.focusSearch(View.FOCUS_DOWN)
            next?.requestFocus()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            val currentFocusedView = currentFocus
            val next = currentFocusedView?.focusSearch(View.FOCUS_UP)
            next?.requestFocus()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}