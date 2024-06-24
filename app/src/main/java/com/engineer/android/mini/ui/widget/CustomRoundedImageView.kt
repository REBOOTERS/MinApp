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
    private var topLeftRadius = 120f
    private var topRightRadius = 120f
    private var bottomLeftRadius = 120f
    private var bottomRightRadius = 120f

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

        paint.color = Color.RED
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // 定义绘制路径

        // 定义绘制路径
        path.reset() // 重置路径


        // 左上角开始
        path.moveTo(0f, topLeftRadius) // 移动到左上角的圆角开始位置
        path.arcTo(RectF(0f, 0f, topLeftRadius * 2, topLeftRadius * 2), 180f, 90f) // 左上角圆弧


        // 右上角
        path.lineTo(width - topRightRadius, 0f) // 移动到右上角的开始位置
        path.arcTo(
            RectF(width - topRightRadius * 2, 0f, width.toFloat(), topRightRadius * 2),
            270f,
            90f
        ) // 右上角圆弧


        // 右下角
        path.lineTo(width.toFloat(), height - bottomRightRadius) // 移动到右下角的开始位置
        path.arcTo(
            RectF(
                width - bottomRightRadius * 2,
                height - bottomRightRadius * 2,
                width.toFloat(),
                height.toFloat()
            ), 0f, 90f
        ) // 右下角圆弧


        // 左下角
        path.lineTo(bottomLeftRadius, height.toFloat()) // 移动到左下角的开始位置
        path.arcTo(
            RectF(0f, height - bottomLeftRadius * 2, bottomLeftRadius * 2, height.toFloat()),
            90f,
            90f
        ) // 左下角圆弧


        // 闭合路径
        path.close() // 关闭路径


    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        // 应用路径

        canvas.clipPath(path)
        // 绘制ImageView中的图片
        super.onDraw(canvas)

        canvas.drawPath(path, paint)
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
