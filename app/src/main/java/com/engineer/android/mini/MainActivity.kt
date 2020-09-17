package com.engineer.android.mini

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.FullscreenActivity
import com.engineer.android.mini.ui.JetpackActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

@SuppressLint("SetTextI18n")
class MainActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpUi()
        setUpStorageInfo()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpStorageInfo() {
        val sb = StringBuilder()
        val codeCacheDir = this.codeCacheDir.absolutePath
        val cacheDir = this.cacheDir.absolutePath
        val dataDir = this.dataDir.absolutePath
        val filesDir = this.filesDir.absoluteFile
        val picDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        sb.append("codeCacheDir :$codeCacheDir").append("\n\n")
        sb.append("cacheDir :$cacheDir").append("\n\n")
        sb.append("dataDir :$dataDir").append("\n\n")
        sb.append("filesDir :$filesDir").append("\n\n")
        sb.append("picDir :$picDir").append("\n\n")
        storage_info.text = sb
        Log.e(TAG, "setUpStorageInfo:\n $sb")
    }

    private fun setUpUi() {
        systemDayNight()
        jetpack.setOnClickListener { gotoPage(JetpackActivity::class.java) }
        full_screen.setOnClickListener { gotoPage(FullscreenActivity::class.java) }
    }

    /**
     * https://blog.csdn.net/guolin_blog/article/details/106061657
     *
     * Android 10 日夜间模式
     */
    private fun systemDayNight() {
        val modeStr = if (isNightMode()) "夜间" else "日间"
        val current = AppCompatDelegate.getDefaultNightMode()
        current_theme.text = "当前日夜间模式 : $modeStr,mode = $current"
        when (current) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> follow_system.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> force_dark.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> force_light.isChecked = true
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> auto_battery.isChecked = true
            else -> {
            }
        }
        theme_radio.setOnCheckedChangeListener { _, checkedId ->
            var mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            when (checkedId) {
                R.id.follow_system -> mode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                R.id.force_dark -> mode = AppCompatDelegate.MODE_NIGHT_YES
                R.id.force_light -> mode = AppCompatDelegate.MODE_NIGHT_NO
                R.id.auto_battery -> mode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val currentMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentMode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                "夜间模式 On".toast()
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                "夜间模式 Off".toast()
            }
        }
    }


}
