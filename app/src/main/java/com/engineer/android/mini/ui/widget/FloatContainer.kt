package com.engineer.android.mini.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.google.android.material.button.MaterialButton

/**
 * Created on 2021/7/4.
 * @author rookie
 */
class FloatContainer @JvmOverloads
constructor(
    context: Context, attributeSet: AttributeSet? = null,
    style: Int = 0
) : FrameLayout(context, attributeSet, style) {

    private var actionX = 0f
    private var actionY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                actionX = event.x
                actionY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                translationX = translationX + event.x - actionX
                translationY = translationY + event.y - actionY
            }
        }
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            return false
        } else {
            return true
        }
    }
}

class SpecialButton @JvmOverloads
constructor(
    context: Context, attributeSet: AttributeSet? = null,
    style: Int = 0
) : MaterialButton(context, attributeSet, style) {
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.e("SpecialButton", "onTouchEvent() called with: event = $event")

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onTouchEvent(event)
    }
}