package com.engineer.android.mini.ext

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent

/**
 * Created on 2020/10/17.
 * @author rookie
 */

fun <T, R> T.easy(block: (T) -> R, ifNull: () -> Unit) {
    if (this != null) {
        block(this)
    } else {
        ifNull()
    }
}

fun Activity.gotoActivity(targetClass: Class<out Activity>?) {
    targetClass?.easy({
        this.startActivity(Intent(this, it))
    }, {
        "targetClass is Null".toast()
    })
}

fun Service.gotoActivity(targetClass: Class<out Activity>?) {
    targetClass?.easy({
        val intent = Intent(this, it)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
    }, {
        "targetClass is Null".toast()
    })
}

fun Activity.gotoActivity(targetIntent: Intent) {
    this.startActivity(targetIntent)
}