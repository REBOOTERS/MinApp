package com.engineer.android.mini.ui.pure

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.method.ScrollingMovementMethod
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityMainContentBinding
import com.engineer.android.mini.databinding.ActivityPureUiBinding
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.getStatusBarHeight
import com.engineer.android.mini.ext.resizeMarginTop
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.adapter.RecyclerViewActivity
import com.engineer.android.mini.util.JavaUtil


@SuppressLint("SetTextI18n")
class PureUIActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityPureUiBinding
    private lateinit var realBinding: ActivityMainContentBinding

    private var animator: ValueAnimator? = null

    private  var x = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPureUiBinding.inflate(layoutInflater)
        realBinding = viewBinding.includeActivityMainContent
        setContentView(viewBinding.root)
        setUpUi()

        TransitionManager.beginDelayedTransition(realBinding.rootContent)


        animator = ValueAnimator.ofInt(0, 1424 - 312.dp).setDuration(5000)
        animator?.addUpdateListener {
            val value = it.animatedValue as Int
            realBinding.annText.scrollTo(x+value,0)

            Log.e("ddd", "x = ${realBinding.annText.scrollX} , value = $value")
        }
        realBinding.annText.post {
            Log.e("ddd","sss = ${realBinding.annText.paint.measureText(realBinding.annText.text.toString())}")
            x = realBinding.annText.scrollX
            animator?.start()
        }

        realBinding.annTextScroll.setOnTouchListener { v, event ->  true}
        realBinding.realMarquee.isSelected = true

        viewBinding.includeActivityMainContent.imageView.post {
            Log.e(TAG, "view.post: " + viewBinding.includeActivityMainContent.imageView.width)
        }
        Handler(Looper.getMainLooper()).post {
            Log.e(TAG, "handler.post: " + viewBinding.includeActivityMainContent.imageView.width)
        }

        viewBinding.includeActivityMainContent.imageView.setOnClickListener {
            // Transition 动画 https://github.com/xiaweizi/TransitionDemo
            val changeBounds = ChangeBounds()
//            changeBounds.interpolator = LinearInterpolator()
            changeBounds.interpolator = AnticipateInterpolator()
            TransitionManager.beginDelayedTransition(
                viewBinding.includeActivityMainContent.rootContent,
                changeBounds
            )
            val params = viewBinding.includeActivityMainContent.imageView.layoutParams
            val hw: Float =
                viewBinding.includeActivityMainContent.imageView.measuredWidth * 1.0f / viewBinding.includeActivityMainContent.imageView.measuredHeight
            if (viewBinding.includeActivityMainContent.imageView.measuredWidth >= (resources.displayMetrics.widthPixels - 20.dp)) {
                params.width = resources.displayMetrics.widthPixels / 2
                params.height = ((resources.displayMetrics.widthPixels / 2) * (1 / hw)).toInt()
            } else {
                params.width = resources.displayMetrics.widthPixels - 20.dp
                params.height = ((resources.displayMetrics.widthPixels - 20.dp) * (1 / hw)).toInt()
            }
            viewBinding.includeActivityMainContent.imageView.layoutParams = params
        }

        realBinding.layoutAc.setOnClickListener {
            gotoPage(LayoutActivity::class.java)
        }
        realBinding.layoutWrapContent.setOnClickListener {
            gotoPage(WrapContentActivity::class.java)
        }

        viewBinding.includeActivityMainContent.customView.setOnClickListener {
            gotoPage(
                CustomViewActivity::class.java
            )
        }
        realBinding.recyclerViewDemo.setOnClickListener { gotoPage(RecyclerViewActivity::class.java) }
        realBinding.switchView.setOnClickListener { gotoPage(SwitchViewActivity::class.java) }
        viewBinding.includeActivityMainContent.imageView.resizeMarginTop(getStatusBarHeight())

        val p = realBinding.contentImg.layoutParams

        testImageSpan()

        realBinding.rangeSlider.addOnChangeListener { _, value, fromUser ->
            Log.e(
                TAG,
                "onCreate() called with: value = $value, fromUser = $fromUser"
            )
            var v = 300 * value
            p.width = value.toInt()
            realBinding.contentImg.layoutParams = p
            val desc = getString(R.string.long_chinese_content)
            val target = "快乐的日子"
            if (v > 0) {
                v += 20
            }
            val s = JavaUtil.getSpannableString(v, desc)


            val old = BitmapFactory.decodeResource(resources, R.drawable.avatar)
            val bitmap = Bitmap.createScaledBitmap(old, 20.dp, 20.dp, false)
            val imageSpan = CenterImageSpan(this, bitmap)
            val start = desc.indexOf(target)
            s.setSpan(imageSpan, start, start + target.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            realBinding.contentView.text = s
        }
        realBinding.rangeSlider.setValues(0.3f)

        "view level is ${viewLevel(realBinding.rangeSlider)}".toast()
    }

    override fun onContentChanged() {
        super.onContentChanged()
        Log.e(TAG, "onContentChanged() called")
    }

    private fun testImageSpan() {
        val content = "相信吧，快乐的日子将会来临！心儿永 HREO 远向往着未来；现在却常是忧郁。一切都是瞬息，一切都将会过去；而那过去了的，就会成为亲切的怀恋。"
        val target = "HREO"
        val ss = SpannableString(content)

        val d = ResourcesCompat.getDrawable(resources, R.drawable.avatar, null)!!
        Log.e("span_test", "d is ${d.javaClass}")
        Log.e("span_test", " w= ${d.intrinsicWidth},h= ${d.intrinsicHeight}")
        d.setBounds(0, 0, 20.dp, 14.dp)
        val start = content.indexOf(target)
        val imageSpan = ImageSpan(d, ImageSpan.ALIGN_BASELINE)
        ss.setSpan(imageSpan, start, start + target.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        realBinding.testSpan.text = ss
    }


    private fun viewLevel(view: View): Int {
        return if (view.parent == null) {
            0
        } else {
            viewLevel(view.parent as View) + 1
        }
    }

    private class CenterImageSpan(context: Context, bitmap: Bitmap) : ImageSpan(context, bitmap) {

        override fun draw(
            canvas: Canvas,
            text: CharSequence?,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            val fm = paint.fontMetricsInt
            val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2
            canvas.save()
            canvas.translate(x, transY.toFloat())
            drawable.draw(canvas)
            canvas.restore()
        }
    }


    private fun setUpUi() {
        systemDayNight()
        viewBinding.includeActivityMainContent.fullScreen.setOnClickListener {
            gotoPage(
                FullscreenActivity::class.java
            )
        }
    }

    /**
     * https://blog.csdn.net/guolin_blog/article/details/106061657
     *
     * Android 10 日夜间模式
     */
    private fun systemDayNight() {
        val modeStr = if (isNightMode()) "夜间" else "日间"
        val current = AppCompatDelegate.getDefaultNightMode()
        viewBinding.includeActivityMainContent.currentTheme.text =
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

    override fun onDestroy() {
        super.onDestroy()
        animator?.cancel()
    }
}

class OldActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val v = FrameLayout(this)
        setContentView(v)
    }
}
