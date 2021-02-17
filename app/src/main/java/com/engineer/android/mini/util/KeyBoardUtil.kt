package com.engineer.android.mini.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import com.engineer.android.mini.ext.log

object KeyBoardUtil {



    fun checkVisible(view: View, callback: ((Boolean) -> Unit)?) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)

            val screenHeight = view.rootView.height
            "${rect}".log()
            "screenHeight=$screenHeight".log()

            val keyboardHeight = screenHeight - rect.bottom
            if (keyboardHeight > screenHeight * 0.1f) {
                callback?.invoke(true)
            } else {
                callback?.invoke(false)
            }
            //                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    fun showKeyboard(view: View) {
        view.requestFocus()
        val manager = provideInputMethodManager(view)
        manager?.showSoftInput(view, 0)
    }

    fun hideKeyboard(view: View) {
        view.clearFocus()
        val manager = provideInputMethodManager(view)
        manager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun provideInputMethodManager(view: View): InputMethodManager? {
        return view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    }
}