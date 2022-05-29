package com.engineer.android.mini.util

import android.content.Context
import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature
import com.engineer.android.mini.util.model.Item
import com.engineer.android.mini.util.model.KotlinPeople
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
        val jsonStr = JSONObject.toJSONString(people)
    }


    fun parseSpecialJson(context: Context) {
        val specialJson = AndroidFileUtils.getStringFromAssets(context, "special_json.json")
        val map = JSONObject.parseObject(specialJson, Map::class.java)
//        Log.e(TAG, map.javaClass.name)

        for (key in map.keys) {
            // 这里当强转，就是人类意志的胜利
            val item = map[key] as JSONArray
            val list = convertJSONArrayToTypeList(item, Item::class.java)
            Log.d(TAG, "parseSpecialJson() called key = $key, list = $list")
        }
        val result = printBeautyJson(map)
        Log.e(TAG, "parseSpecialJson:\n $result")

    }

    /**
     * 这里的参数是 Any ,因此只要是一个合法的 Json, 无论是字符串还是数据模型都可以打印
     */
    fun printBeautyJson(json: Any): String {
        return JSON.toJSONString(
            json,
            SerializerFeature.PrettyFormat,
            SerializerFeature.SortField,
            SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.WriteMapNullValue,
        )
    }

}