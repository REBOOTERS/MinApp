package com.engineer.android.mini.ui.pure

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.LevelListDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import com.engineer.android.mini.R
import com.engineer.android.mini.better.file.FileChangeWatcher
import com.engineer.android.mini.coroutines.old.log
import com.engineer.android.mini.databinding.ActivityMessyBinding
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.screenWidth
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.proguards.A
import com.engineer.android.mini.proguards.B
import com.engineer.android.mini.proguards.BlankFragment
import com.engineer.android.mini.proguards.Utils
import com.engineer.android.mini.proguards.WEEK
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.behavior.DemoDialogActivity
import com.engineer.android.mini.ui.pure.helper.SimpleCallback
import com.engineer.android.mini.util.ImageUtils
import com.engineer.android.mini.util.JavaUtil
import com.engineer.android.mini.util.NetWorkUtil
import com.engineer.android.mini.util.RxTimer
import com.engineer.common.contract.ContentProviderHelper
import com.engineer.common.utils.SystemTools
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch

class MessyActivity : BaseActivity() {

    private lateinit var realBinding: ActivityMessyBinding

    private var x = 0
    private var animator: ValueAnimator? = null

    private var rxTimer: RxTimer? = null

    private val mainScope = MainScope()

    private val mainHandler = Handler(Looper.getMainLooper())


    private val testLazy by lazy {
        Log.e("RootActivity", "testLazy triggle")
        "just a value"
    }

    private var callback: SimpleCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realBinding = ActivityMessyBinding.inflate(layoutInflater)
        setContentView(realBinding.root)
        testLazy
        setupUI()

        handlerTest()
        timerTest()
        proguardTest()
        SystemTools.getManifestPlaceHolderValue(this)

        mainHandler.postDelayed(Runnable { realBinding.openSysDialog.performClick() }, 4000)

        val devicePolicyManager: DevicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager

//        callback = intent?.getSerializableExtra("callback") as SimpleCallback
        Log.i(TAG, "mac1 ${NetWorkUtil.getWifiMacAddress()}")
        Log.i(TAG, "mac2 ${NetWorkUtil.getMac(this)}")
    }

    private fun timerTest() {
        rxTimer = RxTimer().interval(1000) {
            Log.d(TAG, "timerTest()1 it = $it ")
        }
        realBinding.testTimer.setOnClickListener {
            rxTimer?.cancel()
            rxTimer = RxTimer().interval(500) {
                Log.d(TAG, "timerTest()2 it = $it ")
            }
        }
    }

    private fun handlerTest() {
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

        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(2)
        tasks.forEach {
            Log.e(TAG, "task = $it")
        }
        val process = am.runningAppProcesses
        process.forEach {
            Log.e(TAG, "process = $it")
        }
    }

    private fun proguardTest() {
        val fragment = BlankFragment()
        fragment.arguments?.putString("value", "proguard")
        Utils.test1()
        Utils.test3(this)
        Utils.MyBuilder().setName("this")

        val a = A()
        a.test2()
        val b = B()
        println(b.hashCode())

        val today = WEEK.SUNDAY
        println(today)
        test111(today)

        val week = WEEK.MONDAY
        Log.d(TAG, "week = ${week.javaClass.name}")
        Log.d(TAG, "week = ${week.javaClass.superclass.name}")
        Log.d(TAG, "week = ${week.javaClass.superclass.superclass.name}")
    }

    private fun test111(p: WEEK) {
        when (p) {
            WEEK.MONDAY -> {
                println(11)
            }

            WEEK.TUESDAY -> {
                println(22)
            }

            WEEK.SUNDAY -> {
                println(3333)
            }
        }
    }

    private fun setupUI() {
        val floatArray = floatArrayOf(55f, 55f, 5f, 5f, 5f, 5f, 5f, 5f)
//        realBinding.nullBgTv.background = ShapeDrawable(RoundRectShape(floatArray,null,null))

        val len1 = realBinding.contentView.paint.measureText(realBinding.contentView.text.toString())
        val len = realBinding.annText.paint.measureText(realBinding.annText.text.toString())
        Log.e("len-measure", "len1=$len1,len=$len")


        val w = screenWidth - 24.dp
        animator = ValueAnimator.ofInt(0, (len - w).toInt()).setDuration(3000)
        animator?.addUpdateListener {
            val value = it.animatedValue as Int
            realBinding.annText.scrollTo(x + value, 0)

            Log.e("ddd", "x = ${realBinding.annText.scrollX} , value = $value")
        }
        realBinding.annText.post {
            Log.e(
                "ddd", "sss = ${realBinding.annText.paint.measureText(realBinding.annText.text.toString())}"
            )
            Log.e("ddd", "s = ${realBinding.annTextScroll.width} w=$w")
            x = realBinding.annText.scrollX
            animator?.start()
        }
        realBinding.annTextScroll.setOnTouchListener { v, event -> true }

        realBinding.realMarquee.isSelected = true

        val p = realBinding.contentImg.layoutParams

        testImageSpan()

        realBinding.rangeSlider.addOnChangeListener { _, value, fromUser ->
            Log.e(
                TAG, "onCreate() called with: value = $value, fromUser = $fromUser"
            )
            var v = 300 * value
            p.width = value.toInt()
            realBinding.contentImg.layoutParams = p
            val desc = getString(R.string.long_chinese_content)
            val target = "快乐的日子"
            if (v > 0) {
                v += 20
            }
            val s = JavaUtil.getSpannableString(v, desc)


            val old = BitmapFactory.decodeResource(resources, R.drawable.avatar)
            val bitmap = Bitmap.createScaledBitmap(old, 20.dp, 20.dp, false)
            val imageSpan = CenterImageSpan(this, bitmap)
            val start = desc.indexOf(target)
            s.setSpan(imageSpan, start, start + target.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            realBinding.contentView.text = s

            val lenn = realBinding.contentView.paint.measureText(realBinding.contentView.text.toString())
            Log.e("len-measure", "lenn=$lenn")
        }
        realBinding.rangeSlider.setValues(0.3f)

        "view level is ${viewLevel(realBinding.rangeSlider)}".toast()

        realBinding.phone.clipToOutline = true
        realBinding.slider.addOnChangeListener { _, value, fromUser ->
            Log.e(TAG, "onCreate() called with: value = $value, fromUser = $fromUser")
            realBinding.phone.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    val radius = value * 25.dp.toFloat()
                    outline?.setRoundRect(0, 0, view!!.width, view.height, radius)
                }
            }
        }
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.wallpaper_landscape)
        val cornerBitmap = ImageUtils.toRoundCorner(
            bitmap, 20.dp, ImageUtils.CORNER_BOTTOM_LEFT or ImageUtils
                .CORNER_BOTTOM_RIGHT or ImageUtils.CORNER_TOP_LEFT
        )
        realBinding.phone2.setImageBitmap(cornerBitmap)

        initTimeAnimator()

//        Settings.System.putString(contentResolver, "age", "18")
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            log("find ex: $throwable")
        }
        mainScope.launch(exceptionHandler) {
            FileChangeWatcher.initObserver()
        }
        var i = 0
        realBinding.addFile.setOnClickListener {
            ++i
            FileChangeWatcher.addFile("$i.txt")
        }
        realBinding.deleteFile.setOnClickListener {
            if (i <= 0) {
                return@setOnClickListener
            }
            FileChangeWatcher.deleteFile("$i.txt")
            --i
        }

        levelListDrawableTest()

        realBinding.requestOverlay.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")
            )
//            startActivityForResult(intent, 100)

            startActivityIfNeeded(intent, 1001)
        }
        realBinding.callbackTo.setOnClickListener {
            callback?.onResult("this is from ${this.javaClass.name}")
        }
        realBinding.openWifiSettings.setOnClickListener {
            val intent = Intent()
            intent.action = "android.net.wifi.PICK_WIFI_NETWORK"
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
        }
        realBinding.openDialog.setOnClickListener {
            val intent = Intent(this, DemoDialogActivity::class.java)
            intent.putExtra(DemoDialogActivity.EXTRA_TITLE, "title")
            intent.putExtra(DemoDialogActivity.EXTRA_MESSAGE, "MSG1111111111111111111111")
            intent.putExtra(DemoDialogActivity.EXTRA_LEFT_BTN, "LEFT")
            intent.putExtra(DemoDialogActivity.EXTRA_RIGHT_BTN, "RIGHT")
            startActivity(intent)
        }
        realBinding.openSysDialog.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                val dialog = AlertDialog.Builder(this).setTitle("哈哈").setMessage("this is message")
                    .setPositiveButton("ok") { dialog, _ -> dialog?.dismiss() }
                    .setNegativeButton("cancel") { dialog, _ -> dialog?.dismiss() }.create()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                } else {
                    dialog.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                }
                dialog.show()
            } else {
                realBinding.requestOverlay.performClick()
            }


        }
        realBinding.catchException.setOnClickListener {
            try {
                throw SecurityException("now allow call this")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun levelListDrawableTest() {
        val ids = intArrayOf(R.drawable.ic_baseline_add_24, com.weijiaxing.logviewer.R.drawable.ic_action_close)
        val levelListDrawable = LevelListDrawable()
        for (i in ids.indices) {
            val drawable = resources.getDrawableForDensity(ids[i], resources.displayMetrics.densityDpi)
            levelListDrawable.addLevel(0, i, drawable)
        }
        realBinding.levelListTest.setImageDrawable(levelListDrawable)
        realBinding.levelListTest.setOnClickListener {
            realBinding.levelListTest.setImageLevel(1 - realBinding.levelListTest.drawable.level)
        }
        val pakcageInfo = packageManager.getPackageInfo(packageName, 0)
        realBinding.versionMsg.text = "${pakcageInfo.versionName} : ${pakcageInfo.packageName}"
    }

    private fun testImageSpan() {
        val content =
            "相信吧，快乐的日子将会来临！心儿永 HREO 远向往着未来；现在却常是忧郁。一切都是瞬息，一切都将会过去；而那过去了的，就会成为亲切的怀恋。"
        val target = "HREO"
        val ss = SpannableString(content)

        val d = ResourcesCompat.getDrawable(resources, R.drawable.avatar, null)!!
        Log.e("span_test", "d is ${d.javaClass}")
        Log.e("span_test", " w= ${d.intrinsicWidth},h= ${d.intrinsicHeight}")
        d.setBounds(0, 0, 20.dp, 14.dp)
        val start = content.indexOf(target)
        val imageSpan = ImageSpan(d, ImageSpan.ALIGN_BASELINE)
        ss.setSpan(imageSpan, start, start + target.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        realBinding.testSpan.text = ss
    }

    private fun viewLevel(view: View): Int {
        return if (view.parent == null) {
            0
        } else {
            viewLevel(view.parent as View) + 1
        }
    }

    private class CenterImageSpan(context: Context, bitmap: Bitmap) : ImageSpan(context, bitmap) {

        override fun draw(
            canvas: Canvas,
            text: CharSequence?,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            val fm = paint.fontMetricsInt
            val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2
            canvas.save()
            canvas.translate(x, transY.toFloat())
            drawable.draw(canvas)
            canvas.restore()
        }
    }


    private fun initTimeAnimator() {
        val TOTAL_TIME = 4 * 1000
        val timeAnimator = ValueAnimator()
        timeAnimator.setIntValues(TOTAL_TIME, 0)
        timeAnimator.duration = TOTAL_TIME.toLong()
        timeAnimator.interpolator = LinearInterpolator()
        timeAnimator.addUpdateListener {
            val time = it.animatedValue as Int
            updateProgress(time)
        }
        timeAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                updateProgress(TOTAL_TIME)
            }
        })

        realBinding.progressBar.max = TOTAL_TIME
        realBinding.progressBar.setOnClickListener {
            timeAnimator.start()
        }
        ContentProviderHelper.writeValueToDb(this, "name", "mike")
        ContentProviderHelper.writeValueToDb(this, "address", "beijing")
        ContentProviderHelper.writeValueToDb(this, "grade", "1")

//        Log.e(TAG, "value = ${ContentProviderReaderHelper.read(this, "name")}")
    }

    private fun updateProgress(progress: Int) {
        realBinding.progressBar.progress = progress
    }


    override fun onDestroy() {
        super.onDestroy()
        animator?.cancel()
        rxTimer?.cancel()
        mainHandler.removeCallbacksAndMessages(null)
        FileChangeWatcher.stopObserve()
    }
}

class OldActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val v = FrameLayout(this)
        setContentView(v)
    }
}


