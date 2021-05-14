package com.engineer.android.mini.ui.pure

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.engineer.android.mini.R
import kotlinx.android.synthetic.main.activity_switch_view.*

class SwitchViewActivity : AppCompatActivity() {


    private var hasChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_view)


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
    }

    private fun switchView() {
        val pRemote = remote_view.layoutParams
        val pLocal = local_view.layoutParams

        remote_view.layoutParams = pLocal
        local_view.layoutParams = pRemote
        if (hasChange) {
            remote_view.z = 2f
            local_view.z = 1f
        } else {
            local_view.z = 2f
            remote_view.z = 1f
        }

        hasChange = !hasChange
    }
}