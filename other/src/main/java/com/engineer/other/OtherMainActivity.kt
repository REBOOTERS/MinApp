package com.engineer.other

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.engineer.common.contract.ContentProviderHelper
import com.engineer.common.utils.ApplySigningUtils
import com.engineer.other.skip.SkipActivity
import com.engineer.other.ui.EmptyActivity
import com.engineer.other.ui.FakeOldWayActivity
import com.engineer.other.ui.FakePureUiActivity
import com.engineer.other.ui.OpenByOtherActivity

class OtherMainActivity : AppCompatActivity() {
    private val TAG = "OtherMainActivity_TAG"
    private lateinit var tv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
        setContentView(R.layout.activity_other_main)

        tv = findViewById(R.id.tv)

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

        val value = ContentProviderHelper.read(this, "name")
        Log.e(TAG, "onCreate: value =$value")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "currentProcess pid=" + Process.myPid())
        Log.e(TAG, "currentProcess uid=" + Process.myUid())
        Log.e(TAG, "currentProcess tid=" + Process.myTid())
        Log.e(TAG, "currentProcess is64Bit=" + Process.is64Bit())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e(
                TAG, "currentProcess isApplicationUid=" + Process.isApplicationUid(Process.myUid())
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.e(TAG, "currentProcess isIsolated=" + Process.isIsolated())
        }
        Log.e(TAG, "currentProcess myUserHandle=" + Process.myUserHandle())
        Log.e(TAG, "currentProcess getElapsedCpuTime=" + Process.getElapsedCpuTime())
        Log.d(TAG, "onResume() called")

        val sign = ApplySigningUtils.getRawSignatureStr(this, this.packageName)
        Toast.makeText(this, "sign is $sign", Toast.LENGTH_SHORT).show()
        Log.e(TAG, "sign is $sign")
        tv.text = sign
        // 0fa75170f41a70c0ba39427cd0da8842 debug
        // 11c33d4fece7565736c68b98efc78601 release
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun finish() {
        super.finish()
        Log.d(TAG, "finish() called")
    }
}