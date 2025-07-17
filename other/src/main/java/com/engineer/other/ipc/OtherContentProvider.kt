package com.engineer.other.ipc

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log

class OtherContentProvider : ContentProvider() {
    private val TAG = "OtherContentProvider"

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        Log.i(
            TAG,
            "call() called with: method = $method, arg = $arg, thread = ${Thread.currentThread().name}"
        )
        when (method) {
            "sum" -> {
                val result = extras?.getInt("sum")
                Log.i(TAG, "sum result is $result")
            }
        }
        return super.call(method, arg, extras)
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException()
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        throw UnsupportedOperationException()
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException()
    }
}