package com.engineer.compose.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.engineer.compose.ui.ChatMessage

class ChatMessageItemPre : PreviewParameterProvider<ChatMessage> {
    override val values: Sequence<ChatMessage>
        get() = provideTestChat().asSequence()
}

fun provideTestChat(): List<ChatMessage> {
    return listOf(
        ChatMessage("1", "aaa", false),
        ChatMessage("2", "bbb", true),
    )
}