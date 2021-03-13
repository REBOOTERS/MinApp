package com.engineer.android.mini.flutter

import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityFlutterRootBinding
import io.flutter.embedding.android.FlutterActivity

class FlutterRootActivity : AppCompatActivity() {


    private lateinit var viewBinding: ActivityFlutterRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFlutterRootBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolbar)
        viewBinding.toolbarLayout.title = title
        viewBinding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        viewBinding.contentScrolling.flutterActivity.setOnClickListener {
            startActivity(
                FlutterActivity
                    .withNewEngine()
                    .initialRoute("/my_route")
                    .build(this)
            );
        }
    }
}