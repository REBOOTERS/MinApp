package com.engineer.android.mini.ext

/**
 * Created on 2020/10/17.
 * @author rookie
 */

public fun <T, R> T.easy(block: (T) -> R, ifNull: () -> Unit) {
    if (this != null) {
        block(this)
    } else {
        ifNull()
    }
}