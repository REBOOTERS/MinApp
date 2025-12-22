package com.engineer.third

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.engineer.gif.thirdlib.R
import com.engineer.third.internal.NativeMethodsFactory
import com.engineer.third.internal.ProcessCppHelper

private const val TAG = "CppActivity"

class CppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cpp)
        findViewById<TextView>(R.id.simple).text = getHello()

        testJNI()
        findViewById<TextView>(R.id.simple).setOnClickListener {
            ProcessCppHelper.startCommand(this)
        }
    }

    private fun testJNI() {
        val nativeMethod = NativeMethodsFactory()
        val plus = nativeMethod.plus(10, 100)
        Log.d(TAG, "testJNI: called plus = $plus")
        Log.d(TAG, "testJNI: static method ${NativeMethodsFactory.staticPlus(1, 2)}")
        Log.d(TAG, "testJNI: trans  string ${nativeMethod.transToNativeString("Android")}")
    }

    private external fun getHello(): String

    companion object {
        init {
            System.loadLibrary("thirdlib")
        }
    }
}