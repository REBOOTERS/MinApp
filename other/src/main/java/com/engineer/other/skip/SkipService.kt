package com.engineer.other.skip

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.widget.Toast
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log

class SkipService : AccessibilityService() {
    private val TAG = "SkipService"
    override fun onServiceConnected() {
        super.onServiceConnected()
        val config = AccessibilityServiceInfo()
        //配置监听的事件类型为界面变化|点击事件
        config.eventTypes =
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
        serviceInfo = config
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "onAccessibilityEvent() called with: event = $event")
        val nodeInfo = event.source //当前界面的可访问节点信息
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) { //界面变化事件
//            if (event.className.contains("Activity").not()) {
//                return
//            }
            Log.d(TAG, "pkg = ${event.packageName},clz = ${event.className}")
            when (nodeInfo.packageName.toString()) {
                "com.engineer.other" -> {
                    val all = rootInActiveWindow
                    Log.e(TAG, "all is $all")
                    val info =
                        nodeInfo.findAccessibilityNodeInfosByViewId("com.engineer.other:id/jump_ad")
                    Log.e(TAG, "info is $info")
                    skip(info)
                }
                "com.netease.cloudmusic" -> Handler(Looper.getMainLooper()).postDelayed(
                    { skip(nodeInfo.findAccessibilityNodeInfosByViewId("com.netease.cloudmusic:id/c3l")) },
                    500
                ) //0.5秒后执行Runnable中的run方法
                "com.sina.weibo" -> {}
                "com.eric.ontheway" -> skip(nodeInfo.findAccessibilityNodeInfosByViewId("com.eric.ontheway:id/tv_time"))
                "com.walixiwa.flash.player" -> Handler(Looper.getMainLooper()).postDelayed({
                    skip(
                        nodeInfo.findAccessibilityNodeInfosByViewId(
                            "com.walixiwa.flash.player:id/tt_splash_skip_btn"
                        )
                    )
                }, 500) //0.5秒后执行Runnable中的run方法
                "com.huawei.appmarket" -> skip(nodeInfo.findAccessibilityNodeInfosByViewId("com.huawei.appmarket:id/skip_textview"))
                "com.meizu.media.life" -> skip(nodeInfo.findAccessibilityNodeInfosByViewId("com.meizu.media.life:id/tvTimer"))
                "com.qihoo.appstore" -> Handler(Looper.getMainLooper()).postDelayed({
                    skip(
                        nodeInfo.findAccessibilityNodeInfosByViewId(
                            "com.qihoo.plugin.splash:id/ae"
                        )
                    )
                }, 2000) //0.5秒后执行Runnable中的run方法
                "cn.xiaochuankeji.zuiyouLite" -> Handler(Looper.getMainLooper()).postDelayed({
                    skip(
                        nodeInfo.findAccessibilityNodeInfosByViewId(
                            "cn.xiaochuankeji.zuiyouLite:id/btn_skip"
                        )
                    )
                }, 2000) //0.5秒后执行Runnable中的run方法
                "com.tencent.qqlive" -> Handler(Looper.getMainLooper()).postDelayed({
                    commonHandle(nodeInfo)
                }, 2500) //0.5秒后执行Runnable中的run方法
                "com.eg.android.AlipayGphone" -> Handler(Looper.getMainLooper()).postDelayed({
                    skip(
                        nodeInfo.findAccessibilityNodeInfosByViewId(
                            "com.alipay.android.phone.openplatform:id/saoyisao_tv"
                        )
                    )
                }, 2000) //0.5秒后执行Runnable中的run方法
                else -> {
                    commonHandle(nodeInfo)
                }
            }

        }
    }

    private fun commonHandle(nodeInfo: AccessibilityNodeInfo) {
        Log.e(TAG, "start handle pkg = " + nodeInfo.packageName)
        val nodeInfoList = nodeInfo.findAccessibilityNodeInfosByText("跳过")
        Log.e(TAG, "nodeInfoList size = ${nodeInfoList.size}")
        for (info in nodeInfoList) {
            Log.e(TAG, "node is $info")
            val charSequence = info.text
            Log.e(TAG, "char is $charSequence")
            if (charSequence != null) {
                val msg = charSequence.toString()
                if (msg.contains("跳过")) {
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    Toast.makeText(this, "跳过广告", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun skip(nodeInfoList: List<AccessibilityNodeInfo>?) {
        Log.d(TAG, "数据" + nodeInfoList?.size)
        if (nodeInfoList != null && nodeInfoList.isNotEmpty()) {
            nodeInfoList[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Toast.makeText(applicationContext, "跳过广告", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tryGetActivity(componentName: ComponentName): ActivityInfo? {
        return try {
            packageManager.getActivityInfo(componentName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "error = ${e.stackTraceToString()}")
            null
        }
    }

    override fun onInterrupt() {}
}