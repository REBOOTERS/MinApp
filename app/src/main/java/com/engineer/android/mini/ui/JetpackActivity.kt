package com.engineer.android.mini.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import com.engineer.android.mini.jetpack.java.FooFragment
import com.engineer.android.mini.jetpack.kotlin.BarFragment
import kotlinx.android.synthetic.main.activity_jetpack.*
import kotlinx.coroutines.*

class JetpackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jetpack)

        up_fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.up_content, FooFragment.newInstance("Foo", "Foo1"))
                .commitAllowingStateLoss()

            refreshStatus()
        }

        down_fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.down_content, BarFragment.newInstance("Bar", "Bar1"))
                .commitAllowingStateLoss()

            refreshStatus()
        }

    }

    private fun refreshStatus() {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            Log.e("MainActivity", "Current Thread ${Thread.currentThread().name}")
            delay(1000)
            printFragmentStack()
        }
        Log.e(
            "MainActivity",
            "<================= ${supportFragmentManager.fragments.size} fragment =============>"
        )
    }

    private suspend fun printFragmentStack() {
        withContext(Dispatchers.Main) {
            Log.e("MainActivity", "Current Thread ${Thread.currentThread().name}")
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