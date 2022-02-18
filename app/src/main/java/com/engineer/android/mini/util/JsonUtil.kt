package com.engineer.android.mini.util

import android.util.Log
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject


object JsonUtil {
    private const val TAG = "JsonUtil"
    fun <T> convertJSONArrayToTypeList(jsonArray: JSONArray, clazz: Class<T>): List<T> {
        if (jsonArray.isEmpty()) return emptyList()

        val result = ArrayList<T>(jsonArray.size)

        jsonArray.forEach {
            if (it is String || it is Boolean || it is Number) {
                result.add(it as T)
            } else {
                val t = JSONObject.toJavaObject(it as JSONObject, clazz)
                result.add(t)
            }
        }
        return result
    }


    fun parseSpecialJson() {
        val map = JSONObject.parseObject(special_json, Map::class.java)

        for (key in map.keys) {
            val item = map[key] as JSONArray
            val list = convertJSONArrayToTypeList(item, Info::class.java)
            Log.d(TAG, "parseSpecialJson() called key = $key, list = $list")
        }

    }

}

//data class Info(val time: String, val value: String)

class Info {
    val time: String = ""
    val value: String = ""
}

const val special_json = "{\n" +
        "  \"2022-02-01\": [\n" +
        "    {\n" +
        "      \"time\": \"mike\",\n" +
        "      \"value\": \"北京\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"time\": \"mike1\",\n" +
        "      \"value\": \"北京1\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"time\": \"mike2\",\n" +
        "      \"value\": \"北京2\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"2022-02-02\": [\n" +
        "    {\n" +
        "      \"time\": \"lucy\",\n" +
        "      \"value\": \"南京\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"time\": \"lucy1\",\n" +
        "      \"value\": \"南京1\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"2022-02-03\": [\n" +
        "    {\n" +
        "      \"time\": \"lily\",\n" +
        "      \"value\": \"西安\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"time\": \"lily1\",\n" +
        "      \"value\": \"西安1\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"time\": \"lily2\",\n" +
        "      \"value\": \"西安2\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"2022-02-04\": [\n" +
        "    {\n" +
        "      \"time\": \"tom\",\n" +
        "      \"value\": \"tokyo\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"time\": \"tom1\",\n" +
        "      \"value\": \"tokyo1\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"time\": \"tom2\",\n" +
        "      \"value\": \"tokyo2\"\n" +
        "    }\n" +
        "  ]\n" +
        "}"