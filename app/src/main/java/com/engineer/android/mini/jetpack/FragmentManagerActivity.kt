package com.engineer.android.mini.jetpack

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityJetpackBinding
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.jetpack.java.FooFragment
import com.engineer.android.mini.jetpack.kotlin.BarFragment
import com.engineer.android.mini.ui.BaseActivity
import kotlinx.coroutines.*

class FragmentManagerActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityJetpackBinding

    private lateinit var fooFragment: FooFragment

    private var hide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityJetpackBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        fooFragment = FooFragment.newInstance("Foo", "Foo1")
        viewBinding.upFab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.up_content, fooFragment,"zyq_zyq")
                .commitAllowingStateLoss()
            refreshStatus()


        }

        viewBinding.testFab.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentByTag("zyq_zyq")
            val view = fooFragment?.view?.findViewById<View>(R.id.text)
            Log.i(TAG,"view is "+view?.javaClass?.name)
            if (view != null) {
                view.setOnClickListener {
                    "哈哈哈，I fount it".toast()
                }
            }
        }



        viewBinding.upFabMinus.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .remove(fooFragment)
                .commitAllowingStateLoss()

            refreshStatus()
        }

        viewBinding.upFabHide.setOnClickListener {
            if (hide.not()) {
                viewBinding.upFabHide.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24)
                supportFragmentManager.beginTransaction()
                    .hide(fooFragment)
                    .commitAllowingStateLoss()
            } else {
                viewBinding.upFabHide.setImageResource(R.drawable.ic_baseline_remove_red_eye_24)
                supportFragmentManager.beginTransaction()
                    .show(fooFragment)
                    .commitAllowingStateLoss()
            }
            hide = !hide


            refreshStatus()
        }

        viewBinding.downContent.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.down_content, BarFragment.newInstance("Bar", "Bar1"))
                .commitAllowingStateLoss()

            refreshStatus()
        }

        playLifecycle()
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

    private var myComponent: MyLifecycleAwareComponent? = null
    private fun playLifecycle() {
        lifecycle.addObserver(EasyObserver())


        myComponent = MyLifecycleAwareComponent(this)
        myComponent?.init(this)
    }
}