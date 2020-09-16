package com.engineer.android.mini.ext

import android.widget.Toast
import com.engineer.android.mini.MinApp

fun String?.toast() {
    this?.let {
        Toast.makeText(MinApp.INSTANCE, this, Toast.LENGTH_SHORT).show()
    }
}