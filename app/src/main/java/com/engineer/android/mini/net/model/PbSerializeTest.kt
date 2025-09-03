package com.engineer.android.mini.net.model

import android.content.Context
import android.util.Log
import com.engineer.android.mini.proto.AccessTokenData
import com.engineer.android.mini.proto.Configs
import com.engineer.android.mini.proto.Theme
import java.io.File
import java.io.FileOutputStream


object PbSerializeTest {

    private const val TAG = "PbSerializeTest"
    private const val CONFIG_FILE_NAME = "config.pb"

    fun updateConfig(context: Context) {
        val start = System.currentTimeMillis()
        val theme = Theme.THEME_AUTO
        val accessTokenData = AccessTokenData.newBuilder().setAccessToken("890438490234890")
            .setRefreshToken("43894394").setExpiresAtMs(199000).build()
        val historyList = ArrayList<String>()
        for (i in 0 until 10000) {
            historyList.add("history_$i")
        }

        val configs = Configs.newBuilder().setTheme(theme).setAccessTokenData(accessTokenData)
            .addAllListHistory(historyList).build()
        Log.e(TAG,"time0 = ${System.currentTimeMillis() - start}")
        val file = File(context.cacheDir, "config.pb")
        FileOutputStream(file).use { output ->
            configs.writeTo(output)
        }

        Log.e(TAG,"time1 = ${System.currentTimeMillis() - start}")
    }

    private fun saveConfigsToFile(context: Context, configs: Configs) {
        val file = File(context.cacheDir, CONFIG_FILE_NAME)
        try {
            FileOutputStream(file).use { output ->
                configs.writeTo(output)
            }
            Log.d(TAG, "Configs saved successfully to: ${file.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Failed to save configs: ${e.message}")
        }
    }

    // 如果需要读取的方法
    fun readConfigsFromFile(context: Context): Configs? {
        val file = File(context.cacheDir, CONFIG_FILE_NAME)
        return if (file.exists()) {
            try {
                file.inputStream().use { input ->
                    Configs.parseFrom(input)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}
