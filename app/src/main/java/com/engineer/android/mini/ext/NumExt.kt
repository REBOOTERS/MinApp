package com.engineer.android.mini.ext

import android.widget.Toast
import com.engineer.android.mini.MinApp

fun Int?.toast() {
    this?.let {
        Toast.makeText(MinApp.INSTANCE, this.toString(), Toast.LENGTH_SHORT).show()
    }
}