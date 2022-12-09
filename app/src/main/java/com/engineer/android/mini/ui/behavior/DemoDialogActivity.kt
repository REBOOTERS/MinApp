package com.engineer.android.mini.ui.behavior

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.engineer.android.mini.R

class DemoDialogActivity : Activity(), View.OnClickListener {

    private lateinit var titleTv: TextView
    private lateinit var messageTv: TextView
    private lateinit var posBtn: TextView
    private lateinit var negBtn: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_dialog)
        initView()
        initData()
    }

    private fun initData() {
        val title = intent.getStringExtra(EXTRA_TITLE)
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val leftBtn = intent.getStringExtra(EXTRA_LEFT_BTN)
        val rightBtn = intent.getStringExtra(EXTRA_RIGHT_BTN)

        titleTv.text = title
        messageTv.text = message
        posBtn.text = leftBtn
        negBtn.text = rightBtn
    }

    private fun initView() {
        titleTv = findViewById(R.id.dialog_title)
        messageTv = findViewById(R.id.dialog_message)
        posBtn = findViewById(R.id.dialog_left_btn)
        negBtn = findViewById(R.id.dialog_right_btn)
        posBtn.setOnClickListener(this)
        negBtn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        val id = p0?.id
        if (id == R.id.dialog_left_btn) {

            finish()
        } else if (id == R.id.dialog_right_btn) {

            finish()
        }
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_RIGHT_BTN = "extra_right_btn"
        const val EXTRA_LEFT_BTN = "extra_left_btn"
    }
}