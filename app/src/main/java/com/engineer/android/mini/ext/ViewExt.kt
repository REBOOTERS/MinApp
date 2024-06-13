package com.engineer.android.mini.ext

import android.view.View
import android.widget.LinearLayout

fun View?.resizeMarginTop(top: Int = 0) {
    this?.let {
        val params = this.layoutParams
        when (params) {
            is LinearLayout.LayoutParams -> {
                params.topMargin = top
            }
        }
        this.layoutParams = params
    }
    this?.visibility
}

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View?.show() {
    this?.visibility = View.VISIBLE
}

fun View?.resizeWidthWithDp(width: Int) {
    this?.let {
        val params = this.layoutParams
        params.width = width.dp
        this.layoutParams = params
    }
}

fun View?.resizeWidthWithPixel(width: Int) {
    this?.let {
        val params = this.layoutParams
        params.width = width
        this.layoutParams = params
    }
}