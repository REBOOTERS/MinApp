package com.engineer.android.mini.flutter

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.engineer.android.mini.MinApp
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityFlutterRootBinding
import com.engineer.android.mini.ui.BaseActivity
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterFragment

class FlutterRootActivity : BaseActivity() {


    private lateinit var viewBinding: ActivityFlutterRootBinding
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFlutterRootBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.flutterActivity.setOnClickListener {
            startActivity(
                FlutterActivity
                    .withCachedEngine(MinApp.FLUTTER_ENGINE_ID)
                    .build(this)
            )
        }
        viewBinding.flutterFragment.setOnClickListener {
            if (supportFragmentManager.fragments.size > 0) {
                return@setOnClickListener
            }
            currentFragment = FlutterFragment.withCachedEngine(MinApp.FLUTTER_ENGINE_ID).build()
            supportFragmentManager.beginTransaction()
                .replace(R.id.flutter_container, currentFragment!!)
                .commitAllowingStateLoss()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size > 0 && currentFragment != null) {
            supportFragmentManager.beginTransaction().remove(currentFragment!!)
                .commitAllowingStateLoss()
            return
        }
        super.onBackPressed()

    }
}