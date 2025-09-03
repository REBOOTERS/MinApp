package com.engineer.android.mini.net.model


enum class THEME {
    LIGHT, DARK, AUTO
}

data class AccessTokenData(var accessToken: String, var refreshToken: String, var expiresAtMs: Int)

data class Configs(
    var theme: THEME, var accessTokenData: AccessTokenData, var historyList: List<String>
)