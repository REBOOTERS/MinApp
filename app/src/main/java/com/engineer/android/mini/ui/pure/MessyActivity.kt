package com.engineer.android.mini.ui.pure

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityMessyBinding
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.screenWidth
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.util.JavaUtil

class MessyActivity : BaseActivity() {

    private lateinit var realBinding: ActivityMessyBinding

    private var x = 0
    private var animator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realBinding = ActivityMessyBinding.inflate(layoutInflater)
        setContentView(realBinding.root)

        setupUI()
    }

    private fun setupUI() {
        val len = realBinding.annText.paint.measureText(realBinding.annText.text.toString())
        val w = screenWidth - 24.dp
        animator = ValueAnimator.ofInt(0, (len - w).toInt()).setDuration(3000)
        animator?.addUpdateListener {
            val value = it.animatedValue as Int
            realBinding.annText.scrollTo(x + value, 0)

            Log.e("ddd", "x = ${realBinding.annText.scrollX} , value = $value")
        }
        realBinding.annText.post {
            Log.e(
                "ddd",
                "sss = ${realBinding.annText.paint.measureText(realBinding.annText.text.toString())}"
            )
            Log.e("ddd", "s = ${realBinding.annTextScroll.width} w=$w")
            x = realBinding.annText.scrollX
            animator?.start()
        }

        realBinding.annTextScroll.setOnTouchListener { v, event -> true }
        realBinding.realMarquee.isSelected = true

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