package com.engineer.android.mini.ui.behavior

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityBroadcastPageBinding
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity

private const val TAG = "BroadcastPlayground"
class MiniBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        "onReceive() called with: context = $context, intent = $intent".toast()
        Log.d(TAG, "onReceive() called with: context = $context, intent = $intent")
    }
}


class BroadcastPage : BaseActivity() {
    private val action = "com.engineer.android.mini.MINI_UPDATE"
    private lateinit var viewbinding: ActivityBroadcastPageBinding
    private var miniBroadcastReceiver: MiniBroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewbinding = ActivityBroadcastPageBinding.inflate(layoutInflater)
        setContentView(viewbinding.root)

        miniBroadcastReceiver = MiniBroadcastReceiver()

        viewbinding.sendBroadcast.setOnClickListener {
            val intent = Intent()
            intent.action = action
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(action)
        miniBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(it, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        miniBroadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
    }
}
