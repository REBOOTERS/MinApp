package com.engineer.android.mini.ui.behavior.provider

import android.os.Bundle
import android.util.Log
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
    private val TAG = "ContentProviderPage"
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

        ContentProviderReaderHelper.writeValueToDb(this, "name", "mike")
        ContentProviderReaderHelper.writeValueToDb(this, "address", "beijing")
        ContentProviderReaderHelper.writeValueToDb(this, "grade", "1")

//        Log.e(TAG, "value = ${ContentProviderReaderHelper.read(this, "name")}")

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