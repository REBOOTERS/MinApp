package com.engineer.android.mini.ui.pure

import android.content.Context
import android.graphics.*
import android.graphics.drawable.NinePatchDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.util.ImagePool
import kotlinx.android.synthetic.main.activity_custom_view.*


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
            for (i in 0..1000) {
                list.add(ImageView(this))
            }
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
    }
}