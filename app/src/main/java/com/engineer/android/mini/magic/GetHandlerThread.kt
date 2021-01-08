package com.engineer.android.mini.magic

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created on 2021/1/8.
 * @author rookie
 */
class GetHandlerThread(
    name: String, val queue: LinkedBlockingQueue<String>,
    private val listener: AddGetFactory.OnThreadReadyListener?
) : HandlerThread(name) {


    override fun onLooperPrepared() {
        super.onLooperPrepared()
        listener?.onReady()
    }

    fun stopGet() {

    }

    fun startGet() {
        MagicLog.lg("GetThread Blocking....")
        val value = queue.take()
        MagicLog.lg("GetThread get value $value")
    }

    companion object {
        val GET = 0X100
    }
}