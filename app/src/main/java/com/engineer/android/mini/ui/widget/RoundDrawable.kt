package com.engineer.android.mini.ui.widget

import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import com.engineer.android.mini.ext.dp

class RoundDrawable : Drawable() {
    private val TAG = "RoundDrawable"
    private val paint = Paint()
    private val mRoundRect = RectF()

    init {
        paint.isAntiAlias = true
        paint.color = Color.RED
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        Log.d(TAG, "onBoundsChange() called with: bounds = $bounds")
        bounds?.let {
            val padding = 10.dp
            val paddingRound = Rect(
                it.left - padding,
                it.top - padding,
                it.right + padding,
                it.bottom + padding
            )
            mRoundRect.set(paddingRound)
        }
    }


    override fun draw(canvas: Canvas) {
        Log.d(TAG, "draw() called with: mRoundRect = $mRoundRect")
        val radius = 10.dp.toFloat()
        canvas.drawColor(Color.BLUE)
        canvas.translate(10.dp.toFloat(), 10.dp.toFloat())
        canvas.drawRoundRect(mRoundRect, radius, radius, paint)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    @Deprecated("no use", ReplaceWith("PixelFormat.TRANSPARENT", "android.graphics.PixelFormat"),)
    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }
}