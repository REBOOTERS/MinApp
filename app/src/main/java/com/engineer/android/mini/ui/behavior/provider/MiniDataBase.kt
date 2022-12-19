package com.engineer.android.mini.ui.behavior.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MiniDataBase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "onCreate() called with: db = $db")
        db?.execSQL(SQL_CREATE_TABLES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "MiniDataBase"
        private const val DATABASE_NAME = "mini.db"
        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_TABLES =
            "create table if not EXISTS  " +
                    "${MiniContract.Entry.TABLE_NAME} (${MiniContract.Entry._ID}" +
                    " Integer PRIMARY KEY, ${MiniContract.Entry.COLUMN_KEY} TEXT," +
                    "${MiniContract.Entry.COLUMN_VALUE} TEXT) "
    }
}