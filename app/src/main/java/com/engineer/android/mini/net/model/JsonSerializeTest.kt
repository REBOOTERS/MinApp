package com.engineer.android.mini.net.model

import android.content.Context
import android.util.Log
import com.alibaba.fastjson.JSON
import com.engineer.android.mini.util.JsonUtil
import com.engineer.common.utils.AndroidFileUtils
import java.io.File


object JsonSerializeTest {
    private const val TAG = "JsonSerializeTest"

    fun jsonSerialize() {
        val sites = ArrayList<SiteWrapper.Site>()
        for (i in 0..3) {
            val site = SiteWrapper.Site(i.toString(), "mike_$i", i.hashCode().toString())
            sites.add(site)
        }
        val wrapper = SiteWrapper()
        wrapper.token = "2222"
        wrapper.sites = sites
        val result = JSON.toJSONString(wrapper)
        Log.e(TAG, "jsonSerialize: result ${JsonUtil.printBeautyJson(result)}")
    }

    fun jsonDeserialize(context: Context) {
        val jsonString = AndroidFileUtils.getStringFromAssets(context, "mock.json")
        Log.e(TAG, "jsonDeserialize() json = $jsonString")
        val siteWrapper = JSON.parseObject(jsonString, SiteWrapper::class.java)
        Log.e(TAG, "jsonDeserialize() obj  = $siteWrapper")

    }

    fun updateConfig(context: Context) {
        val start = System.currentTimeMillis()
        val theme = THEME.AUTO
        val accessTokenData = AccessTokenData("890438490234890", "43894394", 199000)
        val historyList = ArrayList<String>()
        for (i in 0 until 10000) {
            historyList.add("history_$i")
        }
        Log.e(TAG,"time0 = ${System.currentTimeMillis() - start}")

        val configs = Configs(theme, accessTokenData, historyList)
        val configJson = JSON.toJSONString(configs)
        val file = File(context.cacheDir, "config.json")
        file.bufferedWriter().use { writer ->
            writer.write(configJson)
        }
        Log.e(TAG,"time1 = ${System.currentTimeMillis() - start}")
    }
}
