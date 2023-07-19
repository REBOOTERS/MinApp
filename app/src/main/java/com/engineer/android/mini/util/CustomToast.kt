package com.engineer.android.mini.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.engineer.android.mini.R
import com.facebook.stetho.common.LogUtil
import java.lang.Exception

/**
 * 自定义的 Toast
 * 1. 自定义 view
 * 2. 反射修改 WindowManager.LayoutParams 参数，可以点击
 */
object CustomToast {
    private const val TAG = "CustomToast"

    private var toast: Toast? = null

    fun showToast(context: Context) {
        if (toast == null) {
            toast = Toast(context).apply {
                duration = Toast.LENGTH_LONG
                setGravity(Gravity.TOP, 0, 0)
            }
            makeClickable(toast)
        }
        val customView = LayoutInflater.from(context).inflate(R.layout.msg_custom_tips, null)
        customView.findViewById<View>(R.id.tips_btn).setOnClickListener {
            LogUtil.i(TAG, "click tips")
        }
        customView.setOnClickListener {
            LogUtil.i(TAG, "all click")

        }
        toast?.view = customView
        toast?.show()
        toast = null
    }

    private fun makeClickable(toast: Toast?) {
        if (toast == null) return
        val mTN = getField(toast, "mTN")
        mTN?.let {
            val params = getField(it, "mParams")
            if (params is WindowManager.LayoutParams) {
                LogUtil.i(TAG, "come here")

                params.flags =
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            }
        }
    }

    private fun getField(obj: Any, fileName: String): Any? {
        try {
            val field = obj.javaClass.getDeclaredField(fileName)
            field.isAccessible = true
            return field.get(obj)
        } catch (e: Exception) {
            LogUtil.i(TAG, e.message)
            e.printStackTrace()
        }
        return null
    }

}