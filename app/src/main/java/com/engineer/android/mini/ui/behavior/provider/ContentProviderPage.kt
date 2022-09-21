package com.engineer.android.mini.ui.behavior.provider

import android.content.ContentValues
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.engineer.android.mini.ui.behavior.provider.ui.theme.MiniAppTheme

class ContentProviderPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiniAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }

        writeValueToDb("name", "mike")
        writeValueToDb("address", "beijing")
        writeValueToDb("grade", "1")
    }

    private fun writeValueToDb(key: String, value: String): Boolean {
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
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MiniAppTheme {
        Greeting("Android")
    }
}