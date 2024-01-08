package com.engineer.android.mini.net

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.screenHeight
import com.engineer.android.mini.ext.screenWidth

class SimpleDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_layout, container, false)
    }


    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.apply {
//            setLayout(screenWidth - 100.dp, screenHeight / 2)
            setBackgroundDrawableResource(R.drawable.gradient_bg)
        }
        val attrs = window?.attributes
        attrs?.width = screenWidth - 100.dp
        attrs?.dimAmount = 0.4f
        attrs?.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND and
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND
        window?.attributes = attrs

//        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}