package com.engineer.android.mini.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.engineer.android.mini.R

class VoteWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, style: Int = 0
) : LinearLayout(context, attrs, style) {

    var voteClickListener: VoteClickListener? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_vote_layout, this, false)
        val voteUp = view.findViewById<ImageView>(R.id.vote_up)
        val voteDown = view.findViewById<ImageView>(R.id.vote_down)

        voteUp.setOnClickListener {
            voteClickListener?.voteUp(true)
        }
        voteDown.setOnClickListener {
            voteClickListener?.voteDown(true)
        }
        addView(view)
    }


    interface VoteClickListener {
        fun voteUp(on: Boolean)

        fun voteDown(on: Boolean)
    }
}