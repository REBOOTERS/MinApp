package com.engineer.android.mini.ui.pure

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.widget.DragContainer
import kotlinx.android.synthetic.main.activity_switch_view.*
import kotlinx.android.synthetic.main.drag_content.view.*

class SwitchViewActivity : BaseActivity() {


    private var hasChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_view)


        add_add.setOnClickListener {
            add_add.text.toString().toast()
        }
        plus_plus.setOnClickListener {
            plus_plus.text.toString().toast()
        }

        local_view.setOnClickListener {
            if (hasChange) {
                switchView()
            }
        }
        remote_view.setOnClickListener {
            if (hasChange.not()) {
                switchView()
            }
        }



        val view = LayoutInflater.from(this).inflate(R.layout.drag_content, null, false)
        view.drag_add.setOnClickListener {
            view.drag_iv.alpha = view.drag_iv.alpha + 0.1f
        }
        view.drag_minus.setOnClickListener {
            view.drag_iv.alpha = view.drag_iv.alpha - 0.1f
        }
        view.setOnClickListener { }
        DragContainer.Builder()
            .setActivity(this)//当前Activity，不可为空
            .setDefaultLeft(30)//初始位置左边距
            .setDefaultTop(30)//初始位置上边距
            .setNeedNearEdge(false)//拖动停止后，是否移到边沿
            .setSize(600)//DragView大小
            .setView(view)//设置自定义的DragView，切记不可为空
            .build();
    }

    private fun switchView() {
        val pRemote = remote_view.layoutParams
        val pLocal = local_view.layoutParams

        remote_view.layoutParams = pLocal
        local_view.layoutParams = pRemote
        if (hasChange) {
            remote_view.z = 1f
            local_view.z = 0f
        } else {
            local_view.z = 1f
            remote_view.z = 0f
        }

        hasChange = !hasChange
    }
}