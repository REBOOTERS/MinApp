package com.engineer.android.mini.ext

import android.content.res.Resources
import android.widget.Toast
import com.engineer.android.mini.MinApp

fun Int?.toast() {
    this?.let {
        Toast.makeText(MinApp.INSTANCE, this.toString(), Toast.LENGTH_SHORT).show()
    }
}

val Number.dp get():Int = (this.toInt() * (Resources.getSystem().displayMetrics.density)).toInt()

val Number.px get():Int = (this.toInt() / (Resources.getSystem().displayMetrics.density)).toInt()

val Number.sp get() = (toFloat() * Resources.getSystem().displayMetrics.scaledDensity)