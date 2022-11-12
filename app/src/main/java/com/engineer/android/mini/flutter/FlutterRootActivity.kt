package com.engineer.android.mini.flutter

import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.engineer.android.mini.databinding.ActivityFlutterRootBinding
import com.engineer.android.mini.ui.BaseActivity


class FlutterRootActivity : BaseActivity() {


    private lateinit var viewBinding: ActivityFlutterRootBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFlutterRootBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.flutterActivity.setOnClickListener {
//            startActivity(
//                FlutterActivity
//                    .withCachedEngine(MinApp.FLUTTER_ENGINE_ID)
//                    .build(this)
//            )
        }
        viewBinding.flutterFragment.setOnClickListener {
            if (supportFragmentManager.fragments.size > 0) {
                return@setOnClickListener
            }
//            currentFragment = FlutterFragment.withCachedEngine(MinApp.FLUTTER_ENGINE_ID).build()
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.flutter_container, currentFragment!!)
//                .commitAllowingStateLoss()
        }

        val onBackPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.fragments.size > 0 && currentFragment != null) {
                    supportFragmentManager.beginTransaction().remove(currentFragment!!)
                        .commitAllowingStateLoss()
                }
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressCallback)
    }
}