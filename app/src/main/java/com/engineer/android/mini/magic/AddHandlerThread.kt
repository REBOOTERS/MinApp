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
class AddHandlerThread(
    name: String, val queue: LinkedBlockingQueue<String>,
    private val listener: AddGetFactory.OnThreadReadyListener?
) : HandlerThread(name) {

    private lateinit var addHandler: AddHandler

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        addHandler = AddHandler(looper, queue)
        listener?.onReady()
    }

    fun stopAdd() {

    }

    fun startAdd(value: String) {
        val message = Message.obtain()
        message.what = ADD
        message.obj = value
        addHandler.sendMessage(message)
    }

    class AddHandler(looper: Looper, val queue: LinkedBlockingQueue<String>) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                ADD -> {
                    val value = msg.obj as String
                    queue.add(value)
                    MagicLog.lg("add handler add $value")
                }
            }
        }
    }

    companion object {
        val ADD = 0x100
    }
}