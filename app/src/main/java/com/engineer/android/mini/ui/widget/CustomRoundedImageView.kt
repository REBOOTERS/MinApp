package com.engineer.android.mini.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CustomRoundedImageView : AppCompatImageView {
    private val paint = Paint()
    private val path = Path()
    private var topLeftRadius = 0f
    private var topRightRadius = 0f
    private var bottomLeftRadius = 0f
    private var bottomRightRadius = 0f

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
        init()
    }

    private fun init() {
        paint.isAntiAlias = true
        paint.isFilterBitmap = true

        paint.color  = Color.RED
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 定义绘制路径
        path.reset()
        path.moveTo(0 + topLeftRadius, 0f)
        path.arcTo(RectF(0f, 0f, topLeftRadius * 2, topLeftRadius * 2), -90f, 90f) // 左上角
//        path.lineTo(width - topRightRadius, 0f)
//        path.arcTo(RectF(width - topRightRadius * 2, 0f, width.toFloat(), topRightRadius * 2), -180f, 90f) // 右上角
//        path.lineTo(width.toFloat(), height - bottomRightRadius)
//        path.arcTo(
//            RectF(
//                width - bottomRightRadius * 2, height - bottomRightRadius * 2, width.toFloat(), height.toFloat()
//            ), 0f, 90f
//        ) // 右下角
//        path.lineTo(bottomLeftRadius, height.toFloat())
//        path.arcTo(RectF(0f, height - bottomLeftRadius * 2, bottomLeftRadius * 2, height.toFloat()), 90f, 90f) // 左下角
//        path.close()

    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        // 应用路径
        canvas.drawPath(path,paint)
//        canvas.clipPath(path)
        // 绘制ImageView中的图片
//        super.onDraw(canvas)
        // 恢复画布状态
        canvas.restore()
    }

    fun setCorner(radius: Float) {
        setCornerRadii(radius, radius, radius, radius)
    }

    fun setCornerRadii(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        topLeftRadius = topLeft
        topRightRadius = topRight
        bottomLeftRadius = bottomLeft
        bottomRightRadius = bottomRight
        invalidate() // 重绘视图
    }
}
