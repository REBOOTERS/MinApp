package com.engineer.android.mini.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.LinkedBlockingQueue

/**
 * 对流式返回结果按照 打字机样式单字符显示
 *
 * 1. 包含公式时，可以尽早匹配到格式化的内容
 * 2. 单个字符更新，可以触发 adapter 内容刷新的多次回调，方便滚动处理
 */
object TypewriterHelper {
    private const val TAG = "TypewriterHelper"
    private val handler = Handler(Looper.getMainLooper())
    private val messageQueue = LinkedBlockingQueue<String>()

    private var callback: ((String, Int) -> Unit)? = null

    private var lastText = ""
    private var isEnd = false

    private val typewriterRunnable = object : Runnable {
        override fun run() {

            val message = messageQueue.poll()
            if (message != null) {
//                Log.i(TAG, "msg $message")
                val currentText = lastText
                val newText = currentText + message
                lastText = newText
                Log.i(TAG, "lastText $lastText ,size = ${messageQueue.size}")
                callback?.invoke(lastText, messageQueue.size)
                if (isEnd && messageQueue.isEmpty()) {
                    lastText = ""
                    isEnd = false
                }

            }
            handler.postDelayed(this, TYPING_DELAY)
        }
    }

    fun start(cb: (String, Int) -> Unit) {
        if (callback == null) {
            callback = cb
            handler.post(typewriterRunnable)
        }
    }


    fun reset(checkQueue: Boolean = false) {
        Log.i(TAG, "reset")
        handler.removeCallbacks(typewriterRunnable)
        if (checkQueue && !messageQueue.isEmpty()) {
            var currentText = lastText
            val toList = messageQueue.toList()
            toList.forEach {
                currentText += it
            }
            Log.i(TAG, "reset & output : $currentText")
            callback?.invoke(currentText, 0)
        }
        lastText = ""
        callback = null
        isEnd = false
        messageQueue.clear()
    }

    /**
     * 清空队列，在开启新的对话式调用，防止在新对话中回调上次的内容
     */
    fun clear() {
        Log.i(TAG, "clear")
        messageQueue.clear()
        lastText = ""
    }

    fun addMessage(message: String, end: Boolean) {
        isEnd = end
        messageQueue.offer(message)
    }

    private const val TYPING_DELAY = 20L
}
