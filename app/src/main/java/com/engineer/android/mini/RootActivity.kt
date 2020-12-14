package com.engineer.android.mini

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.engineer.android.mini.coroutines.old.OldWayActivity
import com.engineer.android.mini.ext.gotoActivity
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.jetpack.FragmentManagerActivity
import com.engineer.android.mini.net.RxCacheActivity
import com.engineer.android.mini.ui.behavior.BehaviorActivity
import com.engineer.android.mini.ui.pure.PureUIActivity
import io.flutter.embedding.android.FlutterActivity
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_root.*

class RootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        handlePermissions()

        var blured = false

        blur_view.setOnClickListener {
            if (blured) {
                Blurry.delete(blur_view)
            } else {
                val now = System.currentTimeMillis()
                Blurry.with(this)
                    .radius(25)
                    .sampling(1)
                    .color(Color.argb(66, 0, 255, 255))
                    .async()
                    .onto(blur_view)
                "time is ${System.currentTimeMillis() - now}".toast()
            }
            blured = !blured
        }



        jetpack_ui.setOnClickListener {
            gotoActivity(PureUIActivity::class.java)
        }
        jetpack_arch.setOnClickListener {
            gotoActivity(FragmentManagerActivity::class.java)
        }
        jetpack_behavior.setOnClickListener {
            gotoActivity(BehaviorActivity::class.java)
        }
        coroutines.setOnClickListener {
            gotoActivity(OldWayActivity::class.java)
        }
        cache.setOnClickListener {
            gotoActivity(RxCacheActivity::class.java)
        }
        next.setOnClickListener {
            startActivity(
                FlutterActivity.withCachedEngine(MinApp.MINI).build(this)
            )
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
}