package com.engineer.android.mini.util

import android.content.Context
import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature
import com.engineer.android.mini.net.RxCacheActivity
import com.engineer.android.mini.util.model.KotlinPeople
import com.engineer.android.mini.util.model.PeopleJava
import com.engineer.third.util.AndroidFileUtils


object JsonUtil {
    private const val TAG = "JsonUtil"
    private fun <T> convertJSONArrayToTypeList(jsonArray: JSONArray, clazz: Class<T>): List<T> {
        if (jsonArray.isEmpty()) return emptyList()

        val result = ArrayList<T>(jsonArray.size)

        jsonArray.forEach {
            if (it is String || it is Boolean || it is Number) {
                val element = it as T
                result.add(element)
            } else {
                val t = JSONObject.toJavaObject(it as JSONObject, clazz)
                result.add(t)
            }
        }
        return result
    }

    fun simpleParse() {
        val json = "    {\n" +
                "      \"name\": \"tom2\",\n" +
                "      \"address\": \"tokyo2\"\n" +
                "    }"
        val people = JSONObject.parseObject(json, KotlinPeople::class.java)
        Log.d(TAG, "simpleParse() called people = $people")
    }


    fun parseSpecialJson(context: Context) {
        simpleParse()
        return
        val specialJson = AndroidFileUtils.getStringFromAssets(context, "special_json.json")
        val map = JSONObject.parseObject(specialJson, Map::class.java)

        for (key in map.keys) {
            val item = map[key] as JSONArray
            val list = convertJSONArrayToTypeList(item, PeopleJava::class.java)
            Log.d(TAG, "parseSpecialJson() called key = $key, list = $list")
        }
//        val result = printBeautyJson(JSON.toJSONString(map))
//        Log.e(TAG, "parseSpecialJson: $result")

    }

    fun printBeautyJson(json: String): String {
        val jsonObj = JSONObject.parse(json)
        return JSON.toJSONString(
            jsonObj,
            SerializerFeature.PrettyFormat,
            SerializerFeature.SortField,
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.WriteMapNullValue,
        )
    }

}