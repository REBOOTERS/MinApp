package com.engineer.android.mini.ext

import android.app.Activity
import android.content.Context
import android.content.Intent

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

public fun Activity.gotoActivity(targetClass: Class<out Activity>?) {
    targetClass?.easy({
        this.startActivity(Intent(this, it))
    }, {
        "targetClass is Null".toast()
    })
}

fun Activity.gotoActivity(targetIntent: Intent) {
    this.startActivity(targetIntent)
}