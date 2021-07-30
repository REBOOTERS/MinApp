package com.engineer.android.mini.util

import com.engineer.android.mini.R

/**
 * Created on 2021/1/6.
 * @author rookie
 */
class ImagePool {
    companion object {
        var index = 0

        val images = arrayOf(
            R.drawable.android, R.drawable.android_bot, R.drawable.avatar,
            R.drawable.bot, R.drawable.phone, R.drawable.cute, R.drawable.wall,
            R.drawable.spring
        )

        fun next(): Int {
            val i = index % images.size
            index++
            return images[i]
        }
    }
}