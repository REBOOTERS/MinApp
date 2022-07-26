package com.engineer.android.mini.net

import androidx.annotation.Keep


@Keep
data class WeChatCountList(
    val `data`: List<Data>,
    val errorCode: Int,
    val errorMsg: String
)

@Keep
data class Data(
    val children: List<Any>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)