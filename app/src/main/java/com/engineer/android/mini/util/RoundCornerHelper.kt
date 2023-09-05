package com.engineer.android.mini.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import com.engineer.android.mini.R

class RoundCornerLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    companion object {
        private const val TAG = "RoundCornerLayout"
        private const val CORNER_MODE_OUTLINE = 0
        private const val CORNER_MODE_XFERMODE = 1
        private const val CORNER_MODE_CLIPPATH = 2
    }

    private var cornerMode = 0
    private var topCornerRadius = 0
    private var topCornerRadiusLeft = 0
    private var topCornerRadiusRight = 0
    private var bottomCornerRadius = 0
    private var bottomCornerRadiusLeft = 0
    private var bottomCornerRadiusRight = 0

    private var mRoundRectPath = Path()
    private var mPaint = Paint()
    private val mRect = RectF()
    private var roundedCorners = FloatArray(8)
    private var maskBitmap: Bitmap? = null

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RoundCornerLayout, defStyleAttr, 0)
        cornerMode = typedArray.getInt(
            R.styleable.RoundCornerLayout_cornerMode, CORNER_MODE_OUTLINE
        )
        topCornerRadius =
            typedArray.getDimensionPixelSize(R.styleable.RoundCornerLayout_topCornerRadius, 0)
        topCornerRadiusLeft =
            typedArray.getDimensionPixelSize(R.styleable.RoundCornerLayout_topCornerRadiusLeft, 0)
        topCornerRadiusRight =
            typedArray.getDimensionPixelSize(R.styleable.RoundCornerLayout_topCornerRadiusRight, 0)
        bottomCornerRadius =
            typedArray.getDimensionPixelSize(R.styleable.RoundCornerLayout_bottomCornerRadius, 0)
        bottomCornerRadiusLeft = typedArray.getDimensionPixelSize(
            R.styleable.RoundCornerLayout_bottomCornerRadiusLeft, 0
        )
        bottomCornerRadiusRight = typedArray.getDimensionPixelSize(
            R.styleable.RoundCornerLayout_bottomCornerRadiusRight, 0
        )
        typedArray.recycle()
        mPaint.isAntiAlias = true
        updateRoundRectMode()
    }

    private fun setRoundRectPath() {
        roundedCorners[0] = topCornerRadiusLeft.toFloat()
        roundedCorners[1] = topCornerRadiusLeft.toFloat()
        roundedCorners[2] = topCornerRadiusRight.toFloat()
        roundedCorners[3] = topCornerRadiusRight.toFloat()
        roundedCorners[4] = bottomCornerRadiusLeft.toFloat()
        roundedCorners[5] = bottomCornerRadiusLeft.toFloat()
        roundedCorners[6] = bottomCornerRadiusRight.toFloat()
        roundedCorners[7] = bottomCornerRadiusRight.toFloat()
        mRect.set(0f, 0f, width.toFloat(), height.toFloat())
        mRoundRectPath.rewind()
        mRoundRectPath.addRoundRect(mRect, roundedCorners, Path.Direction.CW)
    }


    private fun setOutlineMode() {//讨巧上下多截出去一点，达到只有上圆角或者下圆角，实际还是一致的圆角
        when {
            topCornerRadius != 0 && bottomCornerRadius == 0 -> {
                clipToOutline = true
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(
                            Rect(0, 0, view.width, view.height + topCornerRadius),
                            topCornerRadius.toFloat()
                        )
                    }
                }
            }

            topCornerRadius == 0 && bottomCornerRadius != 0 -> {
                clipToOutline = true
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(
                            Rect(0, 0 - bottomCornerRadius, view.width, view.height),
                            bottomCornerRadius.toFloat()
                        )
                    }
                }
            }

            topCornerRadius != 0 && bottomCornerRadius != 0 && bottomCornerRadius == topCornerRadius -> {
                clipToOutline = true
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(
                            Rect(0, 0, view.width, view.height), topCornerRadius.toFloat()
                        )
                    }
                }
            }
        }
    }

    private fun updateRoundRectMode() {
        when (cornerMode) {
            CORNER_MODE_OUTLINE -> {
                setOutlineMode()
            }

            else -> clipToOutline = false
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        when (cornerMode) {
            CORNER_MODE_XFERMODE -> {
                maskBitmap?.recycle()
                maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
                    val canvasTmp = Canvas(this)
                    setRoundRectPath()
                    canvasTmp.drawPath(mRoundRectPath, mPaint)
                }
            }

            CORNER_MODE_CLIPPATH -> {
                setRoundRectPath()
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        when (cornerMode) {
            CORNER_MODE_CLIPPATH -> {
                canvas.clipPath(mRoundRectPath) //切割指定区域
                super.dispatchDraw(canvas)
            }

            CORNER_MODE_XFERMODE -> {
                val layerId = canvas.saveLayer(mRect, mPaint) ?: -1
                super.dispatchDraw(canvas)
                mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN) //设置图层混合模式
                maskBitmap?.run {
                    canvas.drawBitmap(this, 0f, 0f, mPaint)
                }
                mPaint.xfermode = null
                canvas.restoreToCount(layerId)
            }

            else -> {
                super.dispatchDraw(canvas)
            }
        }
    }
}
