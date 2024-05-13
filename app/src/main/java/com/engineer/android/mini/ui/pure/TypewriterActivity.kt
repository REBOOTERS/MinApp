package com.engineer.android.mini.ui.pure

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.engineer.android.mini.R
import java.util.concurrent.LinkedBlockingQueue

class TypewriterActivity : AppCompatActivity() {
    private val TAG = "TypewriterActivity"
    private lateinit var typewriterTextView: TypewriterTextView
    private lateinit var tv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_typewriter)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        typewriterTextView = findViewById(R.id.type_writer_tv)
        typewriterTextView.start()

        typewriterTextView.addMessage("Hello Message")
        typewriterTextView.addMessage("Hello Message1")

        findViewById<Button>(R.id.add_content).setOnClickListener {
            getString(R.string.long_chinese_content).forEach {
                Log.i(TAG, "it is -> $it")
                typewriterTextView.addMessage(it.toString())
                TypewriterHelper.addMessage(it.toString(), true)
            }
        }

        tv = findViewById(R.id.tv)
        TypewriterHelper.start {
            tv.text = it
        }
    }
}

object TypewriterHelper {
    private val handler = Handler(Looper.getMainLooper())
    private val messageQueue = LinkedBlockingQueue<String>()

    private var callback: ((String) -> Unit)? = null

    private var lastText = ""
    private var end = false

    private val typewriterRunnable = object : Runnable {
        override fun run() {

            val message = messageQueue.poll()
            Log.i("TypewriterActivity", "message is $message")
            if (message != null) {
                val currentText = lastText
                val newText = currentText + message
                lastText = newText
                callback?.invoke(lastText)
                if (messageQueue.isEmpty() && end) {
                    lastText = ""
                    end = false
                }
            }
            handler.postDelayed(this, TYPING_DELAY)
        }
    }

    fun start(cb: (String) -> Unit) {
        if (callback == null) {
            callback = cb
            handler.post(typewriterRunnable)
        }
    }

    fun addMessage(message: String, end: Boolean) {
        this.end = end
        messageQueue.offer(message)
    }

    fun reset() {
        lastText = ""
    }

    private const val TYPING_DELAY = 50L
}

class TypewriterTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private val handler = Handler(Looper.getMainLooper())
    private val messageQueue = LinkedBlockingQueue<String>()

    private val typewriterRunnable = object : Runnable {
        override fun run() {

            val message = messageQueue.poll()
            if (message != null) {
                val currentText = text.toString()
                val newText = currentText + message
                text = newText
            }
            handler.postDelayed(this, TYPING_DELAY)
        }
    }

    fun start() {
        handler.post(typewriterRunnable)
    }

    fun addMessage(message: String) {
        messageQueue.offer(message)
    }

    companion object {
        private const val TYPING_DELAY = 50L
    }
}