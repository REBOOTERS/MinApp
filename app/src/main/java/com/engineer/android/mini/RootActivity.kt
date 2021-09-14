package com.engineer.android.mini

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.util.Log
import android.util.LogPrinter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.engineer.android.mini.coroutines.old.OldWayActivity
import com.engineer.android.mini.databinding.ActivityRootBinding
import com.engineer.android.mini.ext.gotoActivity
import com.engineer.android.mini.ext.log
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ipc.IpcActivity
import com.engineer.android.mini.jetpack.FragmentManagerActivity
import com.engineer.android.mini.jetpack.LIFECYCLE_TAG
import com.engineer.android.mini.media.MediaActivity
import com.engineer.android.mini.net.RxCacheActivity
import com.engineer.android.mini.net.ThreadExTransform
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.behavior.BehaviorActivity
import com.engineer.android.mini.ui.behavior.lifecycle.PanelActivity
import com.engineer.android.mini.ui.pure.PureUIActivity
import com.engineer.android.mini.util.AndroidSystem
import com.engineer.android.mini.util.ProducerConsumerViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.*
import radiography.Radiography
import java.util.concurrent.TimeUnit

class RootActivity : BaseActivity() {
    // https://mp.weixin.qq.com/s/keR7bO-Nu9bBr5Nhevbe1Q  ViewBinding
    private lateinit var viewBinding: ActivityRootBinding

    private lateinit var mainScope: CoroutineScope

    private val testLazy by lazy {
        Log.e("RootActivity", "testLazy triggle")
        "just a value"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        testLazy
        mainScope = MainScope()
        handlePermissions()
        setupUI()
        printSysInfo()

        mainScope.launch {
            "111".log()
            timeToggle(2000)
            "222".log()
            timeToggle(3000)
            "333".log()
        }


        val handler = Handler(Looper.getMainLooper()) {
            it.what.toString().toast()
            true
        }
        Thread {
            Thread.sleep(1000)
            val message = Message.obtain(handler)
            message.what = 100
            handler.sendMessage(message)
        }.start()

        val id = resources.getIdentifier("jetpack_ui", "id", packageName)
        "id is $id".toast()

        Looper.myQueue().addIdleHandler {
            "ha 😄 idle-handler is work".toast()
            false
        }
    }

    private val disposeOn = CompositeDisposable()


    private fun printSysInfo() {
        val d = Observable.interval(0L,1L,TimeUnit.SECONDS)
            .compose(ThreadExTransform())
            .subscribe {
                val oneMB = 1024 * 1024f
                val sb = StringBuilder()

                val maxMemory = Runtime.getRuntime().maxMemory() / oneMB
                sb.append("maxMemory=").append(maxMemory).append("MB").append("\n")

                val freeMemory = Runtime.getRuntime().freeMemory() / oneMB
                sb.append("freeMemory=").append(freeMemory).append("MB").append("\n")

                val isHarmonyOS = AndroidSystem.isHarmonyOS()
                sb.append("isHarmonyOS : $isHarmonyOS").append("\n")

                val systemTime = System.currentTimeMillis()
                sb.append("System.currentTimeMillis()=$systemTime").append("\n")

                val clockTime = SystemClock.uptimeMillis()
                sb.append("SystemClock.uptimeMillis()=$clockTime").append("\n")
                viewBinding.sysRuntimeInfo.text = sb.toString()
            }
        disposeOn.add(d)
    }

    private fun testPC() {
        if (disposeOn.size() > 0) {
            "producer and consumer is doing".toast()
            return
        }
        val viewModel = ViewModelProvider(this).get(ProducerConsumerViewModel::class.java)
        viewModel.consumer()

        val d = Observable.interval(0, 2, TimeUnit.SECONDS)
            .subscribe {
                viewModel.add(it.toString())
            }

        val d1 = Observable.interval(0, 2500, TimeUnit.MILLISECONDS)
            .subscribe {
                viewModel.consumer()
            }
        disposeOn.add(d1)
        disposeOn.add(d)
    }

    private suspend fun timeToggle(i: Long) {
        withContext(Dispatchers.IO) {
            delay(i)
        }
    }

    private fun setupUI() {
        handleBlur()

        viewBinding.jetpackUi.setOnClickListener {
            gotoActivity(PureUIActivity::class.java)
        }
        viewBinding.jetpackArch.setOnClickListener {
            gotoActivity(FragmentManagerActivity::class.java)
        }
        viewBinding.jetpackBehavior.setOnClickListener {
            gotoActivity(BehaviorActivity::class.java)
        }
        viewBinding.coroutines.setOnClickListener {
            gotoActivity(OldWayActivity::class.java)
        }
        viewBinding.cache.setOnClickListener {
            gotoActivity(RxCacheActivity::class.java)
        }
        viewBinding.next.setOnClickListener {
            gotoActivity(PanelActivity::class.java)
        }
        viewBinding.ipcWay.setOnClickListener {
            gotoActivity(IpcActivity::class.java)
        }
        viewBinding.cp.setOnClickListener {
            testPC()
        }
        viewBinding.media.setOnClickListener { gotoActivity(MediaActivity::class.java) }
        val logger = LogPrinter(Log.DEBUG, "ActivityThread")
        Looper.myLooper()?.setMessageLogging(logger)
    }

    private fun handleBlur() {
        var blur = false

        viewBinding.blurView.setOnClickListener {
            if (blur) {
                Blurry.delete(viewBinding.root)
            } else {
                val now = System.currentTimeMillis()
                Blurry.with(this)
                    .radius(25)
                    .sampling(1)
                    .color(Color.argb(66, 0, 255, 255))
                    .async()
                    .onto(viewBinding.root)
                "time is ${System.currentTimeMillis() - now}".toast()
            }
            blur = !blur
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(LIFECYCLE_TAG, "onResume() called")
        val prettyHierarchy = Radiography.scan()
        Log.e(TAG, "onResume: $prettyHierarchy")
    }

    override fun onPause() {
        super.onPause()
        Log.d(LIFECYCLE_TAG, "onPause() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
        disposeOn.dispose()
    }

    // <editor-fold defaultstate="collapsed" desc="permission">
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
// </editor-fold>
}
