package com.engineer.android.mini.ext

import android.content.Context
import android.content.res.Resources

fun Context?.getStatusBarHeight(): Int {
    var result = 0
    this?.let {
        val resourceId = it.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = it.resources.getDimensionPixelSize(resourceId)
        }
    }
    return result
}

val screenWidth = Resources.getSystem().displayMetrics.widthPixels
val screenHeight = Resources.getSystem().displayMetrics.heightPixels