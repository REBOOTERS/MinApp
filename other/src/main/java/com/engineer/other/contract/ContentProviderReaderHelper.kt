package com.engineer.other.contract

import android.content.ContentValues
import android.content.Context
import android.util.Log

object ContentProviderReaderHelper {


    fun writeValueToDb(context: Context, key: String, value: String): Boolean {
        val contentResolver = context.contentResolver
        var finish = false
        val values = ContentValues()
        values.put(MiniContract.Entry.COLUMN_KEY, key)
        values.put(MiniContract.Entry.COLUMN_VALUE, value)

        var rowId: String? = null

        val cursor = contentResolver.query(MiniContract.Entry.CONTENT_URL, null, null, null, null)
        while (cursor != null && cursor.moveToNext()) {
            val index = cursor.getColumnIndex(MiniContract.Entry.COLUMN_KEY)

            if (index >= 0) {
                val columnKey = cursor.getString(index)
                if (key == columnKey) {
                    val _index = cursor.getColumnIndex(MiniContract.Entry._ID)
                    if (_index >= 0) {
                        rowId = cursor.getString(_index)
                        break
                    }
                }
            }
        }
        if (rowId != null) {
            val where = "_id = ?"
            val whereValue = arrayOf(rowId)
            val rowNum = contentResolver.update(MiniContract.Entry.CONTENT_URL, values, where, whereValue)
            if (rowNum >= 0) {
                finish = true
            }
        } else {
            val uri = contentResolver.insert(MiniContract.Entry.CONTENT_URL, values)
            if (uri != null) {
                finish = true
            }
        }
        cursor?.close()
        return finish
    }

    fun read(context: Context, key: String): String? {
        var result: String? = null
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(MiniContract.Entry.CONTENT_URL, null, null, null, null)

        if (cursor == null) {
            Log.e("OtherMainActivity_TAG", "cursor is null")
            return null
        }
        while (cursor != null && cursor.moveToNext()) {
            val index = cursor.getColumnIndex(MiniContract.Entry.COLUMN_KEY)
            if (index >= 0) {
                val columnKey = cursor.getString(index)
                if (key == columnKey) {
                    val _index = cursor.getColumnIndex(MiniContract.Entry.COLUMN_VALUE)
                    if (_index >= 0) {
                        result = cursor.getString(_index)
                        break
                    }
                }
            }
        }
        cursor?.close()
        return result
    }
}