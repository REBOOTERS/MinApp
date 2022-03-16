package com.engineer.android.mini.ext

import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.engineer.android.mini.MinApp

fun String?.toast() {
    this?.let {
        val toast = Toast.makeText(MinApp.INSTANCE, this, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0.dp, 100.dp)
        toast.show()

    }
}

fun String.log() {
    Log.e("MyLog-${Thread.currentThread().name}", this)
}