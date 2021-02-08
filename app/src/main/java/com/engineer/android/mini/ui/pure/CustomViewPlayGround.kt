package com.engineer.android.mini.ui.pure

import android.content.Context
import android.graphics.*
import android.graphics.drawable.NinePatchDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.util.ImagePool
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.activity_custom_view.*
import kotlin.random.Random


/**
 * Created on 2020/12/29.
 * @author rookie
 */
class SimpleViewOne @JvmOverloads
constructor(
    context: Context, attributeSet:
    AttributeSet? = null, style: Int = 0
) : View(context, attributeSet, style) {

    val paint: Paint = Paint()

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 5.dp.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            it.drawColor(Color.WHITE)
            it.drawCircle(100f, 100f, 100f, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = 210
        val h = 210
        val rW = resolveSize(w, widthMeasureSpec)
        val rH = resolveSize(h, heightMeasureSpec)
        setMeasuredDimension(rW, rH)
    }
}

class SquareImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null, style: Int = 0
) : AppCompatImageView(context, attributeSet, style) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var w = measuredWidth
        var h = measuredHeight

        if (w > h) {
            h = w
        } else {
            w = h
        }
        setMeasuredDimension(w, h)
    }
}


class CustomViewActivity : BaseActivity() {

    companion object {
        // make a memory leak
        private var maybeLeakView: View? = null
    }

    private val list = ArrayList<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)
        maybeLeakView = simple_view
        simple_view.setOnClickListener {
            for (i in 0..10000) {
                list.add(ImageView(this))
            }
            list.size.toString().toast()
        }
        square_iv.setOnClickListener {
            val resId = ImagePool.images.random()
            val resStr = resources.getResourceName(resId)
            "$resId show $resStr".toast()
            square_iv.setImageResource(resId)
        }

        val bitmap = BitmapFactory.decodeFile("sdcard/chatfrom_bg_normal.9.png")
        if (bitmap != null) {
            val chunk = bitmap.ninePatchChunk
            if (NinePatch.isNinePatchChunk(chunk)) {
                val drawable = NinePatchDrawable(resources, bitmap, chunk, Rect(), null)

                Log.e(TAG, "drawable is $drawable")
            }
        }

        setupFlexBox()
    }

    private fun setupFlexBox() {
        var tv: TextView? = null
        val random = Random(10000)
        for (i in 0..10) {
            tv = TextView(this)
            tv.setTextColor(Color.BLACK)
            tv.setPadding(8.dp, 4.dp, 8.dp, 4.dp)
            tv.setBackgroundResource(R.drawable.tv_tag_bg)
            tv.text = random.nextInt().toString()
            val p = FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            p.setMargins(10.dp, 10.dp, 10.dp, 10.dp)
            flexbox.addView(tv, p)
        }
        // 用一种 hack 的方式给 FlexboxLayout 添加分割线
        special_line.post {
            val parmas = special_line.layoutParams as ConstraintLayout.LayoutParams
            val viewHeight = tv?.measuredHeight ?: 0
            parmas.bottomMargin = 20.dp + viewHeight
            special_line.layoutParams = parmas
        }

    }
}