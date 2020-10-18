package com.engineer.android.mini

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.engineer.android.mini.ext.easy
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.JetpackActivity
import kotlinx.android.synthetic.main.activity_root.*

class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        handlePermissions()

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

    private fun handlePermissions() {
        val permissionsToRequire = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequire.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequire.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionsToRequire.isEmpty().not()) {
            ActivityCompat.requestPermissions(this, permissionsToRequire.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You must allow all the permissions.", Toast.LENGTH_SHORT)
                        .show()
//                    finish()
                }
            }
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