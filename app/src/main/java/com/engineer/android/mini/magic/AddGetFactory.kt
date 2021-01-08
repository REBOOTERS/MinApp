package com.engineer.android.mini.magic

import android.util.Log
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created on 2021/1/8.
 * @author rookie
 */
class AddGetFactory {
    val queue = LinkedBlockingQueue<String>()
    var getReady = false
    var addReady = false

    private var isRunning = false

    private val onGetReadyListener = object : OnThreadReadyListener {
        override fun onReady() {
            getReady = true
        }
    }

    private val onAddReadyListener = object : OnThreadReadyListener {
        override fun onReady() {
            addReady = true
        }
    }

    private val getHandlerThread = GetHandlerThread("get", queue, onGetReadyListener)

    private val addHandlerThread = AddHandlerThread("add", queue, onAddReadyListener)


    fun isReady(): Boolean {
        return addReady && getReady
    }

    fun start() {
        lg("start")
        if (isRunning) {
            lg("is already run")
        }
        if (isReady()) {
            isRunning = true
            getHandlerThread.start()
            addHandlerThread.start()
        }
    }

    fun stop() {
        lg("stop")
        getHandlerThread.stopGet()
        addHandlerThread.stopAdd()
    }

    private fun lg(msg: String) {
        Log.e(this.javaClass.simpleName, msg)
    }

    interface OnThreadReadyListener {
        fun onReady()
    }
}