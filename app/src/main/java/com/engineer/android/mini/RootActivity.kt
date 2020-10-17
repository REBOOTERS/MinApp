package com.engineer.android.mini

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.engineer.android.mini.ext.easy
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.JetpackActivity
import kotlinx.android.synthetic.main.activity_root.*

class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        jetpack_ui.setOnClickListener {
            gotoActivity(PureUIActivity::class.java)
        }
        jetpack_arch.setOnClickListener {
            gotoActivity(JetpackActivity::class.java)
        }
        jetpack_behavior.setOnClickListener {
            gotoActivity(BehaviorActivity::class.java)
        }
        coroutines.setOnClickListener {
            gotoActivity(null)
        }
    }

    private fun gotoActivity(targetClass: Class<out Activity>?) {
        targetClass.easy({
            startActivity(Intent(this, it))
        }, {
            "Preparing".toast()
        })
    }
}