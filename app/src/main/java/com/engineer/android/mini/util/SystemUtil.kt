package com.engineer.android.mini.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * 系统设置工具类
 */
object SystemUtil {
    private const val TAG = "SystemUtil"
    private const val KEY_SHOW_TOUCHES = "show_touches"
    private const val KEY_POINTER_LOCATION = "pointer_location"

    /**
     * 检查是否有 WRITE_SETTINGS 权限
     *
     * @param context 应用上下文
     * @return true 拥有权限, false 没有权限
     */
    fun canWriteSettings(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else {
            true // Android 6.0 以下默认有权限
        }
    }

    /**
     * 打开系统设置页面，让用户授予 WRITE_SETTINGS 权限
     *
     * @param context 应用上下文
     */
    fun openWriteSettingsPermission(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open write settings permission", e)
        }
    }

    /**
     * 设置触摸点显示状态
     * 等同于: adb shell settings put system show_touches 1/0
     *
     * @param context 应用上下文
     * @param enabled true 显示触摸点, false 隐藏触摸点
     * @return true 设置成功, false 设置失败或无权限
     */
    @SuppressLint("MissingPermission")
    fun setShowTouches(context: Context, enabled: Boolean): Boolean {
        // 先检查权限
        if (!canWriteSettings(context)) {
            Log.w(TAG, "No WRITE_SETTINGS permission, user need to grant it in settings")
            return false
        }

        return try {
            val resolver = context.contentResolver
            val value = if (enabled) 1 else 0
            val success = Settings.System.putInt(resolver, KEY_SHOW_TOUCHES, value)
            Log.d(TAG, "setShowTouches: enabled=$enabled, success=$success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "setShowTouches failed", e)
            false
        }
    }

    /**
     * 获取触摸点显示状态
     *
     * @param context 应用上下文
     * @return true 显示触摸点, false 隐藏触摸点
     */
    fun isShowTouchesEnabled(context: Context): Boolean {
        return try {
            val resolver = context.contentResolver
            Settings.System.getInt(resolver, KEY_SHOW_TOUCHES, 0) != 0
        } catch (e: Exception) {
            Log.e(TAG, "isShowTouchesEnabled failed", e)
            false
        }
    }

    /**
     * 设置指针位置显示状态
     * 等同于: adb shell settings put system pointer_location 1/0
     *
     * @param context 应用上下文
     * @param enabled true 显示指针位置, false 隐藏指针位置
     * @return true 设置成功, false 设置失败或无权限
     */
    @SuppressLint("MissingPermission")
    fun setPointerLocation(context: Context, enabled: Boolean): Boolean {
        if (!canWriteSettings(context)) {
            Log.w(TAG, "No WRITE_SETTINGS permission, user need to grant it in settings")
            return false
        }

        return try {
            val resolver = context.contentResolver
            val value = if (enabled) 1 else 0
            val success = Settings.System.putInt(resolver, KEY_POINTER_LOCATION, value)
            Log.d(TAG, "setPointerLocation: enabled=$enabled, success=$success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "setPointerLocation failed", e)
            false
        }
    }

    /**
     * 获取指针位置显示状态
     *
     * @param context 应用上下文
     * @return true 显示指针位置, false 隐藏指针位置
     */
    fun isPointerLocationEnabled(context: Context): Boolean {
        return try {
            val resolver = context.contentResolver
            Settings.System.getInt(resolver, KEY_POINTER_LOCATION, 0) != 0
        } catch (e: Exception) {
            Log.e(TAG, "isPointerLocationEnabled failed", e)
            false
        }
    }

    /**
     * 通过 Runtime 执行 adb shell 命令设置触摸点显示状态
     * 需要 root 权限或者应用是系统应用
     *
     * @param enabled true 显示触摸点, false 隐藏触摸点
     * @return true 执行成功, false 执行失败
     */
    fun setShowTouchesViaShell(enabled: Boolean): Boolean {
        return try {
            val value = if (enabled) 1 else 0
            val command = "settings put system $KEY_SHOW_TOUCHES $value"
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val exitCode = process.waitFor()
            Log.d(TAG, "setShowTouchesViaShell: enabled=$enabled, exitCode=$exitCode")
            exitCode == 0
        } catch (e: Exception) {
            Log.e(TAG, "setShowTouchesViaShell failed", e)
            false
        }
    }

    /**
     * 通过 Runtime 执行 shell 命令设置指针位置显示状态
     * 需要 root 权限或者应用是系统应用
     */
    fun setPointerLocationViaShell(enabled: Boolean): Boolean {
        return try {
            val value = if (enabled) 1 else 0
            val command = "settings put system $KEY_POINTER_LOCATION $value"
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val exitCode = process.waitFor()
            Log.d(TAG, "setPointerLocationViaShell: enabled=$enabled, exitCode=$exitCode")
            exitCode == 0
        } catch (e: Exception) {
            Log.e(TAG, "setPointerLocationViaShell failed", e)
            false
        }
    }
}
