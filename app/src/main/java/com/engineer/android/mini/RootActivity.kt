package com.engineer.android.mini

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioRecord
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.util.LogPrinter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.engineer.android.mini.better.BetterActivity
import com.engineer.android.mini.better.testfastjson
import com.engineer.android.mini.coroutines.old.OldWayActivity
import com.engineer.android.mini.databinding.ActivityRootBinding
import com.engineer.android.mini.ext.gotoActivity
import com.engineer.android.mini.ext.log
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ipc.IpcActivity
import com.engineer.android.mini.jetpack.FragmentManagerActivity
import com.engineer.android.mini.net.RxCacheActivity
import com.engineer.android.mini.net.ThreadExTransform
import com.engineer.android.mini.ocr.OcrActivity
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.behavior.BehaviorActivity
import com.engineer.android.mini.ui.behavior.lifecycle.PanelActivity
import com.engineer.android.mini.ui.pure.ChangeViewActivity
import com.engineer.android.mini.ui.pure.MessyActivity
import com.engineer.android.mini.ui.pure.PureUIActivity
import com.engineer.android.mini.util.InstrumentationHelper
import com.engineer.android.mini.util.ProducerConsumerViewModel
import com.engineer.common.utils.AndroidSystem
import com.engineer.compose.ui.MainComposeActivity
import com.engineer.compose.ui.util.AudioRecordHelper
import com.engineer.third.CppActivity
import com.permissionx.guolindev.PermissionX
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class RootActivity : BaseActivity() {
    // https://mp.weixin.qq.com/s/keR7bO-Nu9bBr5Nhevbe1Q  ViewBinding
    private lateinit var viewBinding: ActivityRootBinding
    private lateinit var disposeOn: CompositeDisposable

    private lateinit var mainScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        mainScope = MainScope()
        handlePermissions()
        setupUI()
        coroutineTest()
        testfastjson()
    }


    private fun printSysInfo() {
        val d =
            Observable.interval(0L, 1L, TimeUnit.SECONDS).compose(ThreadExTransform()).subscribe {
                val oneMB = 1024 * 1024f
                val sb = StringBuilder()

                sb.append("count=").append(it).append("\n")
                if (it > 10 && (it % 20 == 0L)) {
                    Runtime.getRuntime().gc()
                    "gc() called".toast()
                }
                val maxMemory = Runtime.getRuntime().maxMemory() / oneMB
                sb.append("maxMemory=").append(maxMemory).append("MB").append("\n")

                val totalMemory = Runtime.getRuntime().totalMemory() / oneMB
                sb.append("totalMemory=").append(totalMemory).append("MB").append("\n")

                val freeMemory = Runtime.getRuntime().freeMemory() / oneMB
                sb.append("freeMemory=").append(freeMemory).append("MB").append("\n")

                val availableProcessor = Runtime.getRuntime().availableProcessors()
                sb.append("availableProcessor=").append(availableProcessor).append("\n")

                val isHarmonyOS = AndroidSystem.isHarmonyOS()
                sb.append("isHarmonyOS : $isHarmonyOS").append("\n")

                val systemTime = System.currentTimeMillis()
                sb.append("System.currentTimeMillis()=$systemTime").append("\n")

                val clockTime = SystemClock.uptimeMillis()
                sb.append("SystemClock.uptimeMillis()=$clockTime").append("\n")
                viewBinding.sysRuntimeInfo.text = sb.toString()
            }
        disposeOn.add(d)
        val info = "${BuildConfig.BUILD_TYPE}_${BuildConfig.FLAVOR}_${BuildConfig.VERSION_NAME}"
        viewBinding.versionInfo.text = info
    }

    private fun testPC() {
        val d2 = Observable.create<Int> {
            it.onNext(0)
//            InstrumentationHelper.sendBackKey()
            InstrumentationHelper.openAppSwitch()
            it.onComplete()
        }.subscribeOn(Schedulers.single()).doOnError {
            Log.e(TAG, it.stackTraceToString())
        }.onErrorReturn { 1 }.doOnNext {
            Log.d(TAG, it.toString())
        }.subscribe()
        disposeOn.add(d2)


        if (disposeOn.size() > 1) {
            "producer and consumer is doing".toast()
            return
        }
        val viewModel = ViewModelProvider(this)[ProducerConsumerViewModel::class.java]
        viewModel.consumer()

        val d = Observable.interval(0, 2, TimeUnit.SECONDS).subscribe {
            viewModel.add(it.toString())
        }

        val d1 = Observable.interval(0, 2500, TimeUnit.MILLISECONDS).subscribe {
            viewModel.consumer()
        }
        disposeOn.add(d1)
        disposeOn.add(d)
    }

    private fun coroutineTest() {
        mainScope.launch {
            "111".log()
            timeToggle(1000)
            "222".log()
            timeToggle(2000)
            "333".log()
        }
    }

    private suspend fun timeToggle(i: Long) {
        val job = withContext(Dispatchers.IO + CoroutineName("timeToggle")) {
            delay(i)
            this.javaClass.toString().log()
            this.coroutineContext.javaClass.toString().log()
            this.coroutineContext.isActive.toString().log()
            this.coroutineContext.job.javaClass.toString().log()
            this.coroutineContext.toString().log()
            1
        }
        job.javaClass.toString().log()
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
        viewBinding.compose.setOnClickListener {
            PermissionX.init(this).permissions(Manifest.permission.RECORD_AUDIO)
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        gotoActivity(MainComposeActivity::class.java)
                    }
                }
        }
        viewBinding.cp.setOnClickListener {
            testPC()
        }
        viewBinding.crash.setOnClickListener { throw IllegalStateException() }
        viewBinding.cpp.setOnClickListener { gotoActivity(CppActivity::class.java) }
        viewBinding.better.setOnClickListener { gotoActivity(BetterActivity::class.java) }
        viewBinding.handlerLogSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val logger = LogPrinter(Log.DEBUG, "MiniApp_ActivityThread")
                Looper.myLooper()?.setMessageLogging(logger)
            } else {
                Looper.myLooper()?.setMessageLogging(null)
            }
        }
        viewBinding.killSelf.setOnClickListener {
            finish()
            exitProcess(0)
        }
        viewBinding.openMessy.setOnClickListener {
            gotoActivity(MessyActivity::class.java)
        }
        viewBinding.openChangeView.setOnClickListener {
            gotoActivity(ChangeViewActivity::class.java)
        }
        viewBinding.ocr.setOnClickListener { gotoActivity(OcrActivity::class.java) }
    }

    private fun handleBlur() {
        var blur = false
        viewBinding.blurView.setOnClickListener {
            if (blur) {
                Blurry.delete(viewBinding.root)
            } else {
                val now = System.currentTimeMillis()
                Blurry.with(this).radius(25).sampling(1).color(Color.argb(66, 0, 255, 255)).async()
                    .onto(viewBinding.root)
                "time is ${System.currentTimeMillis() - now}".toast()
            }
            blur = !blur
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        disposeOn = CompositeDisposable()
        printSysInfo()
    }

    override fun onPause() {
        super.onPause()
        val info = null
        Log.d(TAG, "onPause() called" + info + "kkk")
        disposeOn.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    // <editor-fold defaultstate="collapsed" desc="permission">
    private fun handlePermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        if (allPermissionGranted(this, permission)) {
            return
        }
        if (permission.isEmpty().not()) {
            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 0)
        }
    }

    private fun allPermissionGranted(context: Context, permissions: List<String>): Boolean {
        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(
                    context, it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
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
