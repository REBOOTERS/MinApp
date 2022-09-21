package com.engineer.other

import android.content.ContentResolver
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.engineer.other.contract.MiniContract
import com.engineer.other.skip.SkipActivity

class OtherMainActivity : AppCompatActivity() {
    private val TAG = "OtherMainActivity_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
        setContentView(R.layout.activity_other_main)

        findViewById<View>(R.id.open_skip_sample).setOnClickListener {
            startActivity(Intent(this, SkipActivity::class.java))
        }
        findViewById<View>(R.id.open_same_task_activity).setOnClickListener {

            val intents = arrayOfNulls<Intent>(3)
            val i1 = Intent(this, FakeOldWayActivity::class.java)
            i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intents[1] = i1
            val i2 = Intent(this, FakePureUiActivity::class.java)
            i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intents[0] = i2
            val i3 = Intent(this, EmptyActivity::class.java)
//            i3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intents[2] = i3
            startActivities(intents)
        }
        findViewById<View>(R.id.open_by_other).setOnClickListener {
            startActivity(Intent(this, OpenByOtherActivity::class.java))
        }

        val resolver = contentResolver
        val cursor = resolver.query(MiniContract.Entry.CONTENT_URL,null,null,null,null)
        cursor?.close()
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "currentProcess pid=" + Process.myPid())
        Log.e(TAG, "currentProcess uid=" + Process.myUid())
        Log.e(TAG, "currentProcess tid=" + Process.myTid())
        Log.e(TAG, "currentProcess is64Bit=" + Process.is64Bit())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e(TAG, "currentProcess isApplicationUid=" + Process.isApplicationUid(Process.myUid()))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.e(TAG, "currentProcess isIsolated=" + Process.isIsolated())
        }
        Log.e(TAG, "currentProcess myUserHandle=" + Process.myUserHandle())
        Log.e(TAG, "currentProcess getElapsedCpuTime=" + Process.getElapsedCpuTime())
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

class EmptyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = TextView(this)
        text.text = "SimpleTv"
        setContentView(text)
    }
}