package com.engineer.android.mini.ipc.manager

interface BindServiceCallback {

    fun bindSuccess()

    fun bindFail(e: String)
}