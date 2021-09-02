package com.engineer.android.mini.ui.pure

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnticipateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityPureUiBinding
import com.engineer.android.mini.ext.*
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.adapter.RecyclerViewActivity


@SuppressLint("SetTextI18n")
class PureUIActivity : BaseActivity() {
    private lateinit var realBinding: ActivityPureUiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realBinding = ActivityPureUiBinding.inflate(layoutInflater)
        setContentView(realBinding.root)
//        val p = realBinding.root.layoutParams as FrameLayout.LayoutParams
//        p.topMargin = getStatusBarHeight()
//        realBinding.root.layoutParams = p

        TransitionManager.beginDelayedTransition(realBinding.rootContent)

        realBinding.imageView.setOnClickListener { boundsAnimation() }
        realBinding.layoutAc.setOnClickListener { gotoPage(LayoutActivity::class.java) }
        realBinding.layoutWrapContent.setOnClickListener { gotoPage(WrapContentActivity::class.java) }
        realBinding.customView.setOnClickListener { gotoPage(CustomViewActivity::class.java) }
        realBinding.recyclerViewDemo.setOnClickListener { gotoPage(RecyclerViewActivity::class.java) }
        realBinding.switchView.setOnClickListener { gotoPage(SwitchViewActivity::class.java) }
        realBinding.fullScreen.setOnClickListener { gotoPage(FullscreenActivity::class.java) }
        realBinding.messyView.setOnClickListener { gotoPage(MessyActivity::class.java) }
        realBinding.imageView.resizeMarginTop(getStatusBarHeight())

        systemDayNight()
    }

    private fun boundsAnimation() {
        // Transition 动画 https://github.com/xiaweizi/TransitionDemo
        val changeBounds = ChangeBounds()
//            changeBounds.interpolator = LinearInterpolator()
        changeBounds.interpolator = AnticipateInterpolator()
        TransitionManager.beginDelayedTransition(
            realBinding.rootContent,
            changeBounds
        )
        val params = realBinding.imageView.layoutParams
        val hw: Float =
            realBinding.imageView.measuredWidth * 1.0f / realBinding.imageView.measuredHeight
        if (realBinding.imageView.measuredWidth >= (resources.displayMetrics.widthPixels - 20.dp)) {
            params.width = resources.displayMetrics.widthPixels / 2
            params.height = ((resources.displayMetrics.widthPixels / 2) * (1 / hw)).toInt()
        } else {
            params.width = resources.displayMetrics.widthPixels - 20.dp
            params.height = ((resources.displayMetrics.widthPixels - 20.dp) * (1 / hw)).toInt()
        }
        realBinding.imageView.layoutParams = params

        realBinding.imageView.post {
            Log.e(TAG, "view.post: " + realBinding.imageView.width)
        }
        Handler(Looper.getMainLooper()).post {
            Log.e(TAG, "handler.post: " + realBinding.imageView.width)
        }
    }

    override fun onContentChanged() {
        super.onContentChanged()
        Log.e(TAG, "onContentChanged() called")
    }

    /**
     * https://blog.csdn.net/guolin_blog/article/details/106061657
     *
     * Android 10 日夜间模式
     */
    private fun systemDayNight() {
        val modeStr = if (isNightMode()) "夜间" else "日间"
        val current = AppCompatDelegate.getDefaultNightMode()
        realBinding.currentTheme.text =
            "当前日夜间模式 : $modeStr,mode = $current"
        when (current) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> realBinding.followSystem.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> realBinding.forceDark.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> realBinding.forceLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> realBinding.autoBattery.isChecked = true
            else -> {
            }
        }
        realBinding.themeRadio.setOnCheckedChangeListener { _, checkedId ->
            var mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            when (checkedId) {
                R.id.follow_system -> mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                R.id.force_dark -> mode = AppCompatDelegate.MODE_NIGHT_YES
                R.id.force_light -> mode = AppCompatDelegate.MODE_NIGHT_NO
                R.id.auto_battery -> mode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            }
            AppCompatDelegate.setDefaultNightMode(mode)
        }
        var open = true
        var origin = 0
        realBinding.currentTheme.setOnClickListener {
            val p = realBinding.themeRadio.layoutParams
            if (open) {
                origin = realBinding.themeRadio.measuredHeight
                val ani = ValueAnimator.ofInt(origin, 0)
                ani.addUpdateListener {
                    val value = it.animatedValue as Int
                    p.height = value
                    realBinding.themeRadio.layoutParams = p
                }
                ani.start()
            } else {
                val ani = ValueAnimator.ofInt(0, origin)
                ani.addUpdateListener {
                    val value = it.animatedValue as Int
                    p.height = value
                    realBinding.themeRadio.layoutParams = p
                }
                ani.start()
            }
            open = !open
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                "夜间模式 On".toast()
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                "夜间模式 Off".toast()
            }
        }
    }
}


