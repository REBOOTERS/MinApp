package com.engineer.android.mini.net.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.engineer.android.mini.proto.Configs
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<Configs> {

    override val defaultValue = Configs.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): Configs = Configs.parseFrom(input)

    override suspend fun writeTo(t: Configs, output: OutputStream) = t.writeTo(output)
}

val Context.configsDataStore: DataStore<Configs> by dataStore(
    fileName = "configs.pb",
    serializer = UserPreferencesSerializer
)
