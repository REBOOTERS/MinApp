package com.engineer.compose.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.engineer.compose.R
import com.engineer.compose.ui.preview.ChatMessageItemPre
import com.engineer.compose.ui.ui.theme.MiniAppTheme

class ChatActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiniAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {

                    var inputContent by rememberSaveable { mutableStateOf("") }
                    val list = arrayListOf(
                        ChatMessage("1", "aaa", false),
                        ChatMessage("2", "bbb", true),
                    )
                    ChatScreen(messages = list) {
                        inputContent = it
                    }
                }
            }
        }
    }
}

data class ChatMessage(val sender: String, val text: String, val isMine: Boolean)

@Composable
fun ChatMessageItem(@PreviewParameter(ChatMessageItemPre::class) message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isMine) {
            Avatar(resId = R.drawable.bot)
        }
        Text(
            text = "${message.sender}: ${message.text}",
            modifier = Modifier.padding(
                start = if (!message.isMine) 8.dp else 0.dp,
                end = if (message.isMine) 8.dp else 0.dp
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = if (message.isMine) Color.Black else Color.Black
        )
        if (message.isMine) {
            Avatar(resId = R.drawable.user)
        }
    }
}

@Composable
fun Avatar(resId: Int) {
    Image(
        painter = painterResource(resId),
        contentDescription = "null",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
    )
}

@Composable
fun ChatList(messages: List<ChatMessage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp)
    ) {
        items(messages.size) {
            val message = messages[it]
            ChatMessageItem(message = message)
        }
    }
}

@Composable
fun ChatInputBox(onTextChange: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = "",
            onValueChange = onTextChange,
            label = { Text("输入消息") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }

}

@Composable
fun ChatScreen(
    messages: List<ChatMessage>, onSendMessage: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween
    ) {
        ChatList(messages)
        ChatInputBox { text ->
            if (text.isNotBlank()) {

                onSendMessage(text)
                // 清空输入框
                // 这里可能需要一个状态变量来跟踪输入框的内容
            }
        }
    }
}