package com.engineer.android.mini.ui.pure.helper

import java.io.Serializable


interface SimpleCallback : Serializable {
    fun onResult(msg: String)
}