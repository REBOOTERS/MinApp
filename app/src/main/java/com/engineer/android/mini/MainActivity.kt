package com.engineer.android.mini

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.engineer.android.mini.jetpack.java.FooFragment
import com.engineer.android.mini.jetpack.kotlin.BarFragment
import com.engineer.android.mini.proguards.A
import com.engineer.android.mini.proguards.B
import com.engineer.android.mini.proguards.BlankFragment
import com.engineer.android.mini.proguards.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        up_fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.up_content, FooFragment.newInstance("Foo", "Foo1"))
                .commitAllowingStateLoss()

            refreshStatus()
        }

        down_fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.down_content, BarFragment.newInstance("Bar", "Bar1"))
                .commitAllowingStateLoss()

            refreshStatus()
        }


    }

    private fun refreshStatus() {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                Log.e(
                    "MainActivity",
                    "================= ${supportFragmentManager.fragments.size} fragment ============="
                )
                supportFragmentManager.fragments.forEach {
                    Log.e("MainActivity", "fragment = ${it.javaClass.canonicalName}")
                }
                Log.e(
                    "MainActivity",
                    "=======================================================\n"
                )
            }

        }
    }

    override fun onResume() {
        super.onResume()
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
