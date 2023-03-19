package com.engineer.android.mini.ui.behavior

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.common.notification.*
import com.engineer.common.utils.RxBus
import com.permissionx.guolindev.PermissionX


class NotifyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = LinearLayout(this)
        contentView.setPadding(0, 24.dp, 0, 0)
        contentView.orientation = LinearLayout.VERTICAL
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val margin = 10.dp
        param.setMargins(margin, margin, margin, margin)

        val isNotifyEnable = NotificationManagerCompat.from(this).areNotificationsEnabled()

        val tv = TextView(this)
        "notification enable ? $isNotifyEnable".also { tv.text = it }
        contentView.addView(tv, param)


        val openNotifySetting = Button(this)
        openNotifySetting.text = "打开通知设置"
        openNotifySetting.setOnClickListener {
            NotificationHelper.openSetting(this)
        }
        contentView.addView(openNotifySetting, param)

        val simpleNotify = Button(this)
        simpleNotify.text = "simple Notify"
        simpleNotify.setOnClickListener {
            NotificationHelper.showNotification(this)
        }
        contentView.addView(simpleNotify, param)

        val foregroundNotify = Button(this)
        foregroundNotify.text = "foreground Notify"
        foregroundNotify.setOnClickListener {
            val intent = Intent(this, MyForegroundService::class.java)
            intent.putExtra("type", "standard")
            startService(intent)
        }
        contentView.addView(foregroundNotify, param)


        val customForegroundNotify = Button(this)
        customForegroundNotify.text = "custom Foreground Notify"
        customForegroundNotify.setOnClickListener {
            val intent = Intent(this, MyForegroundService::class.java)
            intent.putExtra("type", "custom")
            startService(intent)

            val d = RxBus.getInstance().toObservable(StopForegroundServiceEvent::class.java)
                .subscribe { stopService(intent) }
        }
        contentView.addView(customForegroundNotify, param)

        val progressNotify = Button(this)
        progressNotify.text = "Progress Notify"
        progressNotify.setOnClickListener {
            val intent = Intent(this, MyForegroundService::class.java)
            intent.putExtra("type", "progress")
            startService(intent)

            val d = RxBus.getInstance().toObservable(StopForegroundServiceEvent::class.java)
                .subscribe { stopService(intent) }
        }
        contentView.addView(progressNotify, param)

        val backgroundService = Button(this)
        backgroundService.text = "background service"
        backgroundService.setOnClickListener {
            val intent = Intent(this, MyBackgroundService::class.java)
            intent.putExtra("type", "background")
            startService(intent)
        }
        contentView.addView(backgroundService, param)

        val backgroundProcess = Button(this)
        backgroundProcess.text = "background process"
        backgroundProcess.setOnClickListener {
            val intent = Intent(this, MyBackgroundProcess::class.java)
            startService(intent)
        }
        contentView.addView(backgroundProcess, param)

        setContentView(contentView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionX.init(this).permissions(arrayListOf(Manifest.permission.POST_NOTIFICATIONS)).request { _, _, _ ->

            }
        }
    }
}