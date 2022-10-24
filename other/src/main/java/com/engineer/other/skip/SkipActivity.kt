package com.engineer.other.skip

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.engineer.other.R

class SkipActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        intent = Intent(this@SkipActivity, SkipService::class.java)
        startService(intent)
        val text1 = findViewById<TextView>(R.id.text1)
        val text2 = findViewById<TextView>(R.id.text2)
        text1.setOnClickListener {
            if (!isAccessibilitySettingsOn(
                    this@SkipActivity,
                    SkipService::class.java.canonicalName!!
                )
            ) {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
                //跳转设置打开无障碍
            } else {
                intent = Intent(this@SkipActivity, SkipService::class.java)
                startService(intent)
                //启动服务
            }
        }
        text2.setOnClickListener { stopService(intent) }
        val adView = findViewById<TextView>(R.id.jump_ad)
        adView.setOnClickListener {
            Log.e(TAG, "jump_ad clicked")
            it.postDelayed({ finish() }, 2000)
            finish()
        }
        hideAccessibility(adView)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideAccessibility(adView: View) {
        adView.accessibilityDelegate = object : View.AccessibilityDelegate() {
            override fun performAccessibilityAction(
                host: View,
                action: Int,
                args: Bundle?
            ): Boolean {
                if (action == AccessibilityNodeInfo.ACTION_CLICK ||
                    action == AccessibilityNodeInfo.ACTION_LONG_CLICK
                ) {
                    Log.e(TAG, "accessibility-click has been interrupted !!!")
                    return true
                }
                return super.performAccessibilityAction(host, action, args)
            }
        }
        val accessibilityManager: AccessibilityManager =
            getSystemService(Context.ACCESSIBILITY_SERVICE)
                    as AccessibilityManager
        adView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (accessibilityManager.isEnabled) {
                    //可以直接拦截无障碍的【坐标】点击，不影响用户正常的手动点击
                    Log.e(TAG, "accessibility-action-down made clickable false !!!")
                    adView.isClickable = false
                }
            }
            false
        }
    }

    /**
     * 检测辅助功能是否开启
     *
     * @return boolean
     */
    private fun isAccessibilitySettingsOn(mContext: Context, serviceName: String): Boolean {
        var accessibilityEnabled = 0
        // 对应的服务
        val service = "$packageName/$serviceName"
        //Log.i(TAG, "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
        } catch (e: SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------")
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    Log.v(
                        TAG,
                        "-------------- > accessibilityService :: $accessibilityService $service"
                    )
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        Log.v(
                            TAG,
                            "We've found the correct setting - accessibility is switched on!"
                        )
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
        }
        return false
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}