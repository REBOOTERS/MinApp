package com.engineer.android.mini.ext

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.util.Log

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

fun Activity.gotoActivity(targetClass: Class<out Activity>?, bundle: Bundle? = null) {
    targetClass?.easy({
        val tag = "START_ACTIVITY_${it.simpleName}"
        if (bundle!=null) {
            this.startActivity(Intent(this, it), bundle)
        } else {
            this.startActivity(Intent(this, it))
        }
        Log.d(tag, "startActivity $targetClass")
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
    Log.d("START_ACTIVITY_", "$targetIntent")
}