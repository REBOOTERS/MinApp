package com.engineer.android.mini.ui.pure

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ui.BaseActivity

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
            it.drawColor(Color.GREEN)
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_view)
    }
}