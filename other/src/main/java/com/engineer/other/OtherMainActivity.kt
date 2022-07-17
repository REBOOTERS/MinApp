package com.engineer.other

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class OtherMainActivity : AppCompatActivity() {
    private  val TAG = "OtherMainActivity_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
        setContentView(R.layout.activity_other_main)
//        startActivity(Intent(this,SkipActivity::class.java))
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