package com.engineer.common.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import com.engineer.common.utils.AndroidSystem.isAppForeground
import java.util.*

object OpenTaskManager {


    private const val TAG = "OpenTaskManager"


    fun startApp(mContext: Context, intent: Intent) {
        Log.d(TAG, "startApp() called with: mContext = $mContext, intent = $intent")
        mContext.startService(intent)
    }

    @JvmStatic
    fun startThirdAppService(mContext: Context, force: Boolean = false) {
        Log.e(TAG, "packageName is " + mContext.applicationContext.packageName)
        Log.e(TAG, "isForeground " + isAppForeground(mContext.applicationContext))

        val intent = Intent("com.engineer.other.custom_background_service")
        intent.setPackage("com.engineer.other")
        val deeplink = "{\"processTask\":[{\"name\":\"{{STRING}}\",\"args\":\"{{STRING}}\"}]}"
        val uri = Uri.parse(deeplink)
        Log.e(TAG, "${uri.scheme}")
        Log.e(TAG, "${uri.host}")
        intent.data = uri
        val resolveInfo = mContext.packageManager.resolveService(
            intent,
            PackageManager.GET_RESOLVED_FILTER
        )
        Log.e(TAG, "resolveInfo = $resolveInfo")
        if (resolveInfo != null) {
            if (force) {
                mContext.applicationContext.startService(intent)
            } else {
                startIntentInternal(mContext, intent)
            }
        }
    }

    private fun startIntentInternal(mContext: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e(TAG, "startForegroundService ----> ${intent.`package`}")
            try {
                mContext.applicationContext.startForegroundService(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            mContext.applicationContext.startService(intent)
        }
    }

    @JvmStatic
    fun startOtherApp(mContext: Context) {
        val intent = mContext.packageManager.getLaunchIntentForPackage("com.engineer.other")
        intent?.let {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(intent)
            Log.d(TAG, "startOtherApp() called with: mContext = $mContext, intent = $intent")
        } ?: run {
            Log.e(TAG, "intent is null")
        }

    }

    fun getAllApps(mContext: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val list =
                mContext.packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            Collections.sort(list, Comparator.comparing { o: PackageInfo -> o.packageName })
            for (packageInfo in list) {
                Log.e(TAG, "packageInfo " + packageInfo.packageName)
            }
        }
    }
}