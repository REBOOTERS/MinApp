package com.engineer.android.mini.ext

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory

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

fun Context?.getResImgWH(id: Int): Pair<Int, Int> {
    this?.let {
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, id, bitmapOptions)
        val w = bitmapOptions.outWidth
        val h = bitmapOptions.outHeight
        return Pair(w, h)
    } ?: run {
        return Pair(0, 0)
    }
}

fun Context?.getResImgWHString(id: Int): String {
    this?.let {
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, id, bitmapOptions)
        val w = bitmapOptions.outWidth
        val h = bitmapOptions.outHeight
        return "w=$w,h=$h"
    } ?: run {
        return "w=0,h=0"
    }
}

val screenWidth = Resources.getSystem().displayMetrics.widthPixels
val screenHeight = Resources.getSystem().displayMetrics.heightPixels