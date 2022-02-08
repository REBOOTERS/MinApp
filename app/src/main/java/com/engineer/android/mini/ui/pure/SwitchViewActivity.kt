package com.engineer.android.mini.ui.pure

import android.os.Bundle
import com.engineer.android.mini.databinding.ActivitySwitchViewBinding
import com.engineer.android.mini.databinding.DragContentBinding
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.widget.DragContainer

class SwitchViewActivity : BaseActivity() {
    private lateinit var viewBinding: ActivitySwitchViewBinding

    private var hasChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySwitchViewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        viewBinding.addAdd.setOnClickListener {
            viewBinding.addAdd.text.toString().toast()
        }
        viewBinding.plusPlus.setOnClickListener {
            viewBinding.plusPlus.text.toString().toast()
        }

        viewBinding.localView.setOnClickListener {
            if (hasChange) {
                switchView()
            }
        }
        viewBinding.remoteView.setOnClickListener {
            if (hasChange.not()) {
                switchView()
            }
        }

        val subBinding = DragContentBinding.inflate(layoutInflater)
//        val view = LayoutInflater.from(this).inflate(R.layout.drag_content, null, false)

        subBinding.dragAdd.setOnClickListener {
            subBinding.dragIv.alpha = subBinding.dragIv.alpha + 0.1f
        }
        subBinding.dragMinus.setOnClickListener {
            subBinding.dragIv.alpha = subBinding.dragIv.alpha - 0.1f
        }
        subBinding.root.setOnClickListener { }
        DragContainer.Builder()
            .setActivity(this)//当前Activity，不可为空
            .setDefaultLeft(30)//初始位置左边距
            .setDefaultTop(30)//初始位置上边距
            .setNeedNearEdge(false)//拖动停止后，是否移到边沿
            .setSize(600)//DragView大小
            .setView(subBinding.root)//设置自定义的DragView，切记不可为空
            .build()
    }

    private fun switchView() {
        val pRemote = viewBinding.remoteView.layoutParams
        val pLocal = viewBinding.localView.layoutParams

        viewBinding.remoteView.layoutParams = pLocal
        viewBinding.localView.layoutParams = pRemote
        if (hasChange) {
            viewBinding.remoteView.z = 1f
            viewBinding.localView.z = 0f
        } else {
            viewBinding.localView.z = 1f
            viewBinding.remoteView.z = 0f
        }

        hasChange = !hasChange
    }
}