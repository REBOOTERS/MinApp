package com.engineer.android.mini.util

import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import java.util.concurrent.LinkedBlockingDeque

const val TAG = "ProducerConsumerViewMod"

class ProducerConsumerViewModel(app: Application) : AndroidViewModel(app) {

    private val queue = LinkedBlockingDeque<String>()

    private val product by lazy {
        Producer("producer", queue)
    }

    private val consumer by lazy {
        Consumer("consumer", queue)
    }

    init {
        product.start()
        consumer.start()
    }

    fun add(t: String) {
        product.produce(t)
    }

    fun consumer() {
        consumer.consume()
    }

    private class Consumer<T>(
        name: String,
        val queue: LinkedBlockingDeque<T>
    ) : HandlerThread(name) {

        private val handler by lazy {
            object : Handler(looper) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    when (msg.what) {
                        0 -> {
                            Log.e(TAG, "handleMessage: consumer begin take")
                            val event = queue.take()
                            Log.e(TAG, "handleMessage: consumer take event: $event")
                        }
                    }
                }
            }
        }

        override fun onLooperPrepared() {
            super.onLooperPrepared()
            handler.obtainMessage()
        }

        fun consume() {
            Log.d(TAG, "consume() called")
            handler.sendEmptyMessage(0)
        }
    }

    private class Producer<T>(name: String, val queue: LinkedBlockingDeque<T>) :
        HandlerThread(name) {

        private val handler by lazy {
            object : Handler(looper) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    Log.e(TAG, "handleMessage: producer : $msg")
                    val t: T = msg.obj as T
                    Log.e(TAG, "handleMessage: queue : $queue")
                    queue.offer(t)
                }
            }
        }

        override fun onLooperPrepared() {
            super.onLooperPrepared()
            handler.obtainMessage()
        }

        fun <T> produce(t: T) {
            val message = Message.obtain()
            message.obj = t
            handler.sendMessage(message)
        }

    }

    override fun onCleared() {
        super.onCleared()
        queue.clear()
        product.quitSafely()
        consumer.quitSafely()
    }
}