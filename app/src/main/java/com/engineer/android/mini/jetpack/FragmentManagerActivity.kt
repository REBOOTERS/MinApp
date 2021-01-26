package com.engineer.android.mini.jetpack

import android.os.Bundle
import android.util.Log
import com.engineer.android.mini.R
import com.engineer.android.mini.jetpack.java.FooFragment
import com.engineer.android.mini.jetpack.kotlin.BarFragment
import com.engineer.android.mini.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_jetpack.*
import kotlinx.coroutines.*

class FragmentManagerActivity : BaseActivity() {

    private val fooFragment: FooFragment by lazy {
        FooFragment.newInstance("Foo", "Foo1")
    }

    private var hide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jetpack)

        up_fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.up_content, fooFragment)
                .commitAllowingStateLoss()
            refreshStatus()
        }

        up_fab_minus.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .remove(fooFragment)
                .commitAllowingStateLoss()

            refreshStatus()
        }

        up_fab_hide.setOnClickListener {
            if (hide.not()) {
                up_fab_hide.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24)
                supportFragmentManager.beginTransaction()
                    .hide(fooFragment)
                    .commitAllowingStateLoss()
            } else {
                up_fab_hide.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                supportFragmentManager.beginTransaction()
                    .show(fooFragment)
                    .commitAllowingStateLoss()
            }
            hide = !hide


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
            Log.e("MainActivity", "Current Thread. ${Thread.currentThread().name}")
            delay(1000)
            printFragmentStack()
        }
        Log.e(
            "MainActivity",
            "<================= ${supportFragmentManager.fragments.size} fragment =============>"
        )
    }

    private suspend fun printFragmentStack() {
        Log.e("MainActivity", "Current Thread.. ${Thread.currentThread().name}")

        withContext(Dispatchers.Main) {
            Log.e("MainActivity", "Current Thread... ${Thread.currentThread().name}")
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