package com.engineer.third

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.engineer.gif.thirdlib.R

class CppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cpp)
        findViewById<TextView>(R.id.simple).text = getHello()
    }

    private external fun getHello(): String

    companion object {
        init {
            System.loadLibrary("thirdlib")
        }
    }
}