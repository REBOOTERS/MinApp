package com.engineer.android.mini.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.engineer.android.mini.R

class CustomSwitchTab @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null, style: Int = 0
) : LinearLayout(context, attributeSet, style) {

    private var leftBtn: TextView
    private var rightBtn: TextView
    var onCustomTabClickListener: OnCustomTabClickListener? = null

    private var trigger = false

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_switch_tab, this, false)
        leftBtn = view.findViewById(R.id.left)
        rightBtn = view.findViewById(R.id.right)
        leftBtn.setOnClickListener {
            if (trigger) {
                trigger = !trigger
                leftBtn.setBackgroundResource(R.drawable.left_select)
                rightBtn.setBackgroundResource(R.drawable.right_unselect)
                leftBtn.setTextColor(Color.parseColor("#FFFFFF"))
                rightBtn.setTextColor(Color.parseColor("#CCCCCC"))

                onCustomTabClickListener?.onSelect(leftBtn.text.toString())

            }
        }
        rightBtn.setOnClickListener {
            if (!trigger) {
                trigger = !trigger
                leftBtn.setBackgroundResource(R.drawable.left_unselect)
                rightBtn.setBackgroundResource(R.drawable.right_select)
                leftBtn.setTextColor(Color.parseColor("#CCCCCC"))
                rightBtn.setTextColor(Color.parseColor("#FFFFFF"))

                onCustomTabClickListener?.onSelect(rightBtn.text.toString())
            }

        }
        addView(view)
    }

    interface OnCustomTabClickListener {
        fun onSelect(text: String)
    }
}