package com.engineer.android.mini.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.proguards.A
import com.engineer.android.mini.proguards.B
import com.engineer.android.mini.proguards.BlankFragment
import com.engineer.android.mini.proguards.Utils

/**
 * Created on 2020/9/17.
 * @author rookie
 */
open class BaseActivity : AppCompatActivity() {

    internal val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate() called ")
    }

    internal fun gotoPage(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }

    internal fun isNightMode(): Boolean {
        val flag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume() called")
        proguardTest()
    }

    private fun proguardTest() {
        val fragment = BlankFragment()
        fragment.arguments?.putString("value", "proguard")
        Utils.test1()
        Utils.test3(this)
        Utils.MyBuilder().setName("this")

        val a = A()
        a.test2()
        val b = B()
        println(b.hashCode())
    }
}