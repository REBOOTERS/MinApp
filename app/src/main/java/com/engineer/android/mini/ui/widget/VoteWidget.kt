package com.engineer.android.mini.ui.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IntDef
import com.engineer.android.mini.R

/**
 * 对于 LLM 返回的 Response 进行评价，包含点赞和点踩操作
 */
class VoteWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var voteClickListener: VoteClickListener? = null

    private var voteUp: ImageView
    private var voteDown: ImageView

    private var currentVoteState: Int = VOTE_NONE

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_vote_layout, this, false)
        voteUp = view.findViewById<ImageView>(R.id.vote_up)
        voteDown = view.findViewById<ImageView>(R.id.vote_down)

        voteUp.setOnClickListener {

            if (currentVoteState == VOTE_UP) {
                voteClickListener?.voteUp(false)
            } else {
                voteClickListener?.voteUp(true)
            }
        }
        voteDown.setOnClickListener {
            if (currentVoteState == VOTE_DOWN) {
                voteClickListener?.voteDown(false)
            } else {
                voteClickListener?.voteDown(true)
            }
        }
        addView(view)
    }


    interface VoteClickListener {
        fun voteUp(on: Boolean)

        fun voteDown(on: Boolean)
    }

    /**
     * 根据操作更新 UI
     */
    fun updateState(@VoteState state: Int) {
        when (state) {
            VOTE_NONE -> {
                voteUp.setColorFilter(Color.parseColor("#000"))
                voteDown.setColorFilter(Color.parseColor("#000"))
            }

            VOTE_UP -> {
                voteUp.setColorFilter(Color.parseColor("#329DFF"))
                voteDown.setColorFilter(Color.parseColor("#000"))
            }

            VOTE_DOWN -> {
                voteUp.setColorFilter(Color.parseColor("#000"))
                voteDown.setColorFilter(Color.parseColor("#329DFF"))
            }
        }
    }

    companion object {
        // 没有任何操作
        const val VOTE_NONE = 0

        // 进行了点赞
        const val VOTE_UP = 1

        // 进行了点踩
        const val VOTE_DOWN = 2
    }

    @IntDef(VOTE_NONE, VOTE_UP, VOTE_DOWN)
    @Retention(AnnotationRetention.SOURCE)
    annotation class VoteState


}

