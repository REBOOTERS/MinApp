package com.engineer.android.mini.coroutines.old

import androidx.room.TypeConverter
import com.alibaba.fastjson.JSONObject

object AddressTypeConvert {

    @TypeConverter
    fun addressToString(address: Address): String {
        return JSONObject.toJSONString(address)
    }

    @TypeConverter
    fun stringToAddress(json: String): Address {
        return JSONObject.parseObject(json, Address::class.java)
    }
}