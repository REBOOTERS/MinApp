package com.engineer.android.mini.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.engineer.android.mini.R

class RecommendWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, style: Int = 0
) : LinearLayout(context, attrs, style) {

    var itemClickListener: ItemClickListener? = null

    fun setData(items: List<String>) {
        orientation = VERTICAL
        items.forEach {
            val view = LayoutInflater.from(context).inflate(R.layout.widget_recommend_layout, this, false)
            val tv: TextView = view.findViewById(R.id.content)
            tv.text = it
            view.setOnClickListener {
                itemClickListener?.onClickUp(tv.text.toString())
            }
            addView(view)
        }
    }


    interface ItemClickListener {
        fun onClickUp(item: String)
    }
}