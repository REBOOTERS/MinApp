package com.engineer.android.mini.magic

import android.util.Log

/**
 * Created on 2021/1/8.
 * @author rookie
 */
object MagicLog {
    fun lg(msg: String) {
        Log.e(this.javaClass.simpleName, msg)
    }
}