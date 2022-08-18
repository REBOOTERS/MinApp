package com.engineer.other

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.engineer.other.skip.SkipActivity

class OtherMainActivity : AppCompatActivity() {
    private  val TAG = "OtherMainActivity_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
        setContentView(R.layout.activity_other_main)

        findViewById<View>(R.id.open_skip_sample).setOnClickListener {
            startActivity(Intent(this, SkipActivity::class.java))
        }
        findViewById<View>(R.id.open_same_task_activity).setOnClickListener {
            startActivity(Intent(this,FakePureUiActivity::class.java))
        }
        findViewById<View>(R.id.open_by_other).setOnClickListener {
            startActivity(Intent(this,OpenByOtherActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun finish() {
        super.finish()
        Log.d(TAG, "finish() called")
    }
}