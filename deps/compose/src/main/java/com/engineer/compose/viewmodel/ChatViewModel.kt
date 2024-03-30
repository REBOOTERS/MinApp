package com.engineer.compose.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    private val TAG = "ChatViewModel"

    val handler = Handler(Looper.getMainLooper())

    private val _messageList = MutableLiveData<ArrayList<ChatMessage>>()


    val messageList: LiveData<ArrayList<ChatMessage>> = _messageList

    val kkk = MutableLiveData<String>()
    fun query(userQuery: String) {
        val history = _messageList.value ?: ArrayList()
        val userMsg = ChatMessage("IAM四十二", userQuery, true)
        history.add(userMsg)
        _messageList.value = history

        requestResult(userQuery)

    }

    fun requestResult(userQuery: String) {
        val kk =
            "通常在Activity中接收来自ViewModel的数据变更如果以上建议都不能解决你的问题，那么可能需要更深入地检查你的代码逻辑和架构，以找到导致页面不刷新的具体原因。".toCharArray()
        val sb = StringBuilder()

        Thread {
            for (c in kk) {
                val history = _messageList.value ?: ArrayList()
                val lastMsg = history.last()
                sb.append(c)
                Log.d(TAG, "sb = $sb")
                kkk.postValue(sb.toString())
                if (lastMsg.sender == "Bot") {
                    val newMsg = ChatMessage("Bot", sb.toString(), false)
                    history[history.size - 1] = newMsg
                } else {
                    val newMsg = ChatMessage("Bot", sb.toString(), false)
                    history.add(newMsg)
                }
                Log.d(TAG, "history $history")
                
                _messageList.postValue(history)

                Thread.sleep(100)
            }
        }.start()
    }
}