package com.engineer.common.notification

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.engineer.common.R
import com.engineer.common.utils.OpenTaskManager
import com.engineer.common.utils.RxBus

interface NotificationBuilder {

    fun provideNotification(context: Context): NotificationCompat.Builder
    fun provideChannelId(): String = ""
    fun provideChannelName(): String = "Channel-One"
    fun provideChannelDesc(): String = "MiniApp-Channel-One"
}

class SimpleNotification : NotificationBuilder {

    val CHANNEL_ID = "CHANNEL_ID_123456"
    private val textTitle = "通知"
    private val textContent = "通知内容"

    override fun provideChannelId(): String {
        return CHANNEL_ID
    }

    override fun provideNotification(context: Context): NotificationCompat.Builder {
        val receiverStop = Intent(context, SimpleBroadcastReceiver::class.java)
        receiverStop.action = "stop_action"
        receiverStop.putExtra("channelId", CHANNEL_ID)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, receiverStop, PendingIntent.FLAG_IMMUTABLE
        )
        val receiver = Intent(context, SimpleBroadcastReceiver::class.java)
        receiver.action = "start_action"
        receiver.putExtra("start_id", System.currentTimeMillis())
        val pendingIntent1 = PendingIntent.getBroadcast(
            context, 0, receiver, PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(textTitle).setContentText(textContent).setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Much longer text that cannot fit one line " + ",longer text that cannot fit one line ..."
                )
            ).setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_baseline_stop_24, "停止", pendingIntent)
            .addAction(R.drawable.ic_baseline_play_arrow_24, "开始", pendingIntent1)
    }

}

class MyForegroundNotification : NotificationBuilder {

    val CHANNEL_ID = "CHANNEL_ID_abcdefg"
    private val textTitle = "前台通知"
    private val textContent = "前台通知内容"

    override fun provideNotification(context: Context): NotificationCompat.Builder {

        return NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_baseline_notifications_red_24)
            .setContentTitle(textTitle).setContentText(textContent).setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "前台通知内容 前台通知内容前台通知内容前台通知内容前台通知内容 " + ",前台通知内容前台通知内容前台通知内容..."
                )
            ).setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    override fun provideChannelId(): String {
        return CHANNEL_ID
    }

    override fun provideChannelName(): String {
        return "前台通知渠道"
    }

    override fun provideChannelDesc(): String {
        return "前台通知渠道 Description"
    }
}

class CustomNotification : NotificationBuilder {
    val CHANNEL_ID = "CHANNEL_ID_custom"
    override fun provideNotification(context: Context): NotificationCompat.Builder {
        val packageName = context.packageName
        val customView = RemoteViews(packageName, R.layout.custom_notification_foreground_layout)
        val receiverStop = Intent(context, SimpleBroadcastReceiver::class.java)
        receiverStop.action = "stop_service"
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, receiverStop, PendingIntent.FLAG_IMMUTABLE
        )
        customView.setOnClickPendingIntent(R.id.close_notify, pendingIntent)

        return NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_baseline_notifications_red_24)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()).setCustomContentView(customView)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    override fun provideChannelId(): String {
        return CHANNEL_ID
    }


    override fun provideChannelName(): String {
        return "自定义-前台通知渠道"
    }

    override fun provideChannelDesc(): String {
        return "自定义-前台通知渠道 Description"
    }
}

class SimpleBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "SimpleBroadcastReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e(
            TAG, "onReceive() called with:, " + "action = ${intent?.action} ," + "extra = ${
                intent?.getStringExtra("channelId") ?: intent?.getStringExtra("start_id")
            }"
        )
        Log.d(TAG, "onReceive() called with: context = $context, intent = $intent")
        when (intent?.action) {
            "stop_service" -> {
                RxBus.getInstance().post(StopForegroundServiceEvent())
            }
        }
    }
}

class MyForegroundService : Service() {
    private val TAG = "MyForegroundService"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind() called with: intent = $intent")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(
            TAG, "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId"
        )
        val type = intent?.getStringExtra("type") ?: "standard"

        val notification = NotificationHelper.provideForegroundNotification(this, type)
        startForeground(System.currentTimeMillis().toInt(), notification)
//        stopForeground(true)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

}

class MyBackgroundService : Service() {
    private val TAG = "MyBackgroundService"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind() called with: intent = $intent")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(
            TAG, "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId"
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        super.onDestroy()
    }
}

class MyBackgroundProcess : Service() {
    private val TAG = "MyBackgroundProcess"

    override fun onCreate() {
        super.onCreate()
        Log.e(
            TAG, "process pid " + Process.myPid() + ",is64Bit= " + Process.is64Bit()
        )
        Log.d(TAG, "onCreate() called")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind() called with: intent = $intent")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(
            TAG, "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId"
        )
        val intent = Intent(this, MyBackgroundService::class.java)
        OpenTaskManager.startApp(this, intent)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
        super.onDestroy()
    }
}

object NotificationHelper {

    private fun createNotificationChannel(
        context: Context, builder: NotificationBuilder
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = builder.provideChannelName()
            val descriptionText = builder.provideChannelDesc()
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(builder.provideChannelId(), name, importance).apply {
                description = descriptionText
                vibrationPattern = longArrayOf(1, 0, 1)
                enableVibration(true)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context) {
        val simpleNotification = SimpleNotification()
        val builder = simpleNotification.provideNotification(context)
        createNotificationChannel(context, simpleNotification)
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
    }

    fun provideForegroundNotification(context: Context, type: String): Notification {

        val myForegroundNotification = if (type == "standard") MyForegroundNotification() else CustomNotification()
        val builder = myForegroundNotification.provideNotification(context)
        createNotificationChannel(context, myForegroundNotification)
        return builder.build()
    }

    fun openSetting(context: Context?) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
        }
        intent.data = Uri.fromParts("package", context?.packageName, null)

        try {
            context?.startActivity(intent)
        } catch (e: Exception) {
            tryThis(context)
            e.printStackTrace()
        }
    }

    private fun tryThisOne(context: Context?) {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"

        //for Android 5-7
        intent.putExtra("app_package", context?.packageName)
        intent.putExtra("app_uid", context?.applicationInfo?.uid)

        // for Android O
        intent.putExtra("android.provider.extra.APP_PACKAGE", context?.packageName)


        try {
            context?.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun tryThis(context: Context?) {
        val intent = Intent()
        when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1 -> {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("android.provider.extra.APP_PACKAGE", context?.packageName)
            }
            else -> {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", context?.packageName)
                intent.putExtra("app_uid", context?.applicationInfo?.uid)
            }
        }

        try {
            context?.startActivity(intent)
        } catch (e: Exception) {
            tryThisOne(context)
            e.printStackTrace()
        }
    }
}

class StopForegroundServiceEvent {}
