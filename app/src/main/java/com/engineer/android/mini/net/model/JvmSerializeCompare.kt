package com.engineer.android.mini.net.model

import com.alibaba.fastjson.JSON
import com.engineer.android.mini.proto.AccessTokenData
import com.engineer.android.mini.proto.Configs
import com.engineer.android.mini.proto.Theme
import java.io.File
import java.io.FileOutputStream

object JvmSerializeCompare {

    @JvmStatic
    fun main(args: Array<String>) {
        updateConfig()

        updateConfig2()
    }

    fun updateConfig() {
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
        println("time0 = ${System.currentTimeMillis() - start}")
        val file = File("config.pb")
        FileOutputStream(file).use { output ->
            configs.writeTo(output)
        }
        println("time1 = ${System.currentTimeMillis() - start}")
    }


    fun updateConfig2() {
        val start = System.currentTimeMillis()
        val theme = com.engineer.android.mini.net.model.THEME.AUTO
        val accessTokenData = com.engineer.android.mini.net.model.AccessTokenData(
            "890438490234890", "43894394", 199000
        )
        val historyList = ArrayList<String>()
        for (i in 0 until 10000) {
            historyList.add("history_$i")
        }
        val configs =
            com.engineer.android.mini.net.model.Configs(theme, accessTokenData, historyList)
        val configJson = JSON.toJSONString(configs)


        println("time0 = ${System.currentTimeMillis() - start}")
        val file = File("config.json")
        file.bufferedWriter().use { writer ->
            writer.write(configJson)
        }
        println("time1 = ${System.currentTimeMillis() - start}")
    }

}