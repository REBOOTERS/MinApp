package com.engineer.android.mini.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
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

        transparentStatusBar()
        fullStatusBar()
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

    /**
     * 透明状态栏
     */
    protected fun transparentStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.TRANSPARENT
    }

    /**
     * 内容填空状态栏
     */
    protected fun fullStatusBar() {

        val window = window
        val decorView = window.decorView
        decorView.setOnApplyWindowInsetsListener { v, insets ->
            val defaultInsets = v.onApplyWindowInsets(insets)
            defaultInsets?.let {

                val left = it.systemWindowInsetLeft
                val top = it.systemWindowInsetTop
                val right = it.systemWindowInsetRight
                val bottom = it.systemWindowInsetBottom
                Log.e(TAG, "insets: $left,$top,$right,$bottom")
            }

            defaultInsets.replaceSystemWindowInsets(
                defaultInsets.getSystemWindowInsetLeft(),
                0,
                defaultInsets.getSystemWindowInsetRight(),
                defaultInsets.getSystemWindowInsetBottom()

            )
        }
        ViewCompat.requestApplyInsets(decorView)
    }

    fun printAny(vararg strs: String) {
        strs.forEach {
            Log.e(TAG, it)
        }
    }
}