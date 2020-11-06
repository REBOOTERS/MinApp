package com.engineer.android.mini.net

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.zchu.rxcache.diskconverter.IDiskConverter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.reflect.Type

class KotlinGsonConvert : IDiskConverter {

    private val gson = Gson()


    override fun <T : Any?> load(source: InputStream?, type: Type?): T? {
        val adapter: TypeAdapter<out Any> = gson.getAdapter(TypeToken.get(type))
        val jsonReader = gson.newJsonReader(InputStreamReader(source))
        val value = adapter.read(jsonReader) as T?
        return value
    }

    override fun writer(sink: OutputStream?, data: Any?): Boolean {
        val string: String = gson.toJson(data)
        val bytes = string.toByteArray()
        sink?.write(bytes, 0, bytes.size)
        sink?.flush()
        return true
    }
}