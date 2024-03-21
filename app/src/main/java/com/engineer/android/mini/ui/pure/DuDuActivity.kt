package com.engineer.android.mini.ui.pure

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.engineer.android.mini.R
import com.engineer.common.utils.AndroidFileUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.random.Random

fun Long.toTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(date)
}

object LoadingUtil {
    private const val WORDS = "正在努力思考中..."
    val arrays = WORDS.toCharArray()
    val length = arrays.size
    var index = 0
    val sb = StringBuilder()
    var running = false
    var callback: ((String) -> Unit)? = null
    val handle = Handler(Looper.getMainLooper())
    var content = ""
    private val runnable = object : Runnable {
        override fun run() {
            if (running) {
                if (index < length) {
                    val word = arrays[index % length]
                    sb.append(word)
                    callback?.invoke(sb.toString())
                    content = sb.toString()
                } else {

                    if (content == "正在努力思考中...") {
                        content = "正在努力思考中"
                    } else if (content == "正在努力思考中") {
                        content = "正在努力思考中."
                    } else if (content == "正在努力思考中.") {
                        content = "正在努力思考中.."
                    } else if (content == "正在努力思考中..") {
                        content = "正在努力思考中..."
                    }
                    callback?.invoke(content)
                }
                handle.postDelayed(this, 300)
                index++
            }

        }
    }

    fun start(callback: ((String) -> Unit)? = null) {
        running = true
        this.callback = callback

        handle.removeCallbacks(runnable)
        handle.postDelayed(runnable, 300)
    }

    fun stop() {
        callback?.invoke("")
        callback = null
        handle.removeCallbacks(runnable)
        running = false
        index = 0
        handle.removeCallbacks(runnable)
        sb.clear()
    }
}

class DuDuActivity : AppCompatActivity() {
    private val TAG = "DuDuActivity_TAG"
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_du_du)

        var running = false
        val loadingTv = findViewById<TextView>(R.id.loading_tv)
        val controlBtn = findViewById<Button>(R.id.loading_control)
        controlBtn.setOnClickListener {
            if (running) {
                controlBtn.text = "start"
                LoadingUtil.stop()
            } else {
                LoadingUtil.start {
                    loadingTv.text = it
                }
                controlBtn.text = "stop"
            }
            running = !running
        }

        findViewById<Button>(R.id.test_t).setOnClickListener {
            testT()
        }
        findViewById<Button>(R.id.test_usage_manager).setOnClickListener {
            val usageManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val start = System.currentTimeMillis() - 3600000
            val end = System.currentTimeMillis()

            val active = usageManager.isAppInactive(packageName)
            val events = usageManager.queryEvents(start, end)
            val dd = usageManager.queryConfigurations(UsageStatsManager.INTERVAL_BEST, start, end)

            Log.e(TAG, "active $active,events = $events,config = $dd")


            val appOps: AppOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
            if (mode != MODE_ALLOWED) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                return@setOnClickListener
            }
//

            val result = usageManager.queryAndAggregateUsageStats(start, end)
            for (mutableEntry in result) {
                val usageStats = mutableEntry.value
                val info = String.format(
                    "%-40s,%-20s,%-20s,%-20s,%-20s",
                    usageStats.packageName,
                    usageStats.firstTimeStamp.toTime(),
                    usageStats.lastTimeStamp.toTime(),
                    usageStats.lastTimeUsed.toTime(),
                    usageStats.totalTimeInForeground.toString()
                )
                Log.e(TAG, info)
            }
        }
        findViewById<View>(R.id.gradient_view).setOnClickListener {
            refreshView()
        }
    }

    private fun refreshView() {
        val json = AndroidFileUtils.getStringFromAssets(this, "colors.json")
        val colorMap = JSON.parseObject(json)
        val colors = IntArray(9)
        colors.forEachIndexed { index, _ ->
            val value = Random.nextInt(0, 255)
            colors[index] = value
        }
//        val colors = intArrayOf(9, 127, 46, 46, 127, 9, 207, 209)
        Log.i(TAG, colors.joinToString())
        val colorList = ArrayList<Int>()
        for (color in colors) {
            val value = colorMap.getString(color.toString())
            Log.i(TAG, "color  $value")
            val dd = Color.parseColor("#${value}")
            Log.i(TAG, "dd     $dd")
            colorList.add(dd)
        }
        val results = colorList.toIntArray()
        val gradient = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, results)
        gradient.gradientType = GradientDrawable.LINEAR_GRADIENT
        val view = findViewById<View>(R.id.gradient)
        view.background = gradient

        val gradient1 = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, results)
        gradient1.gradientType = GradientDrawable.RADIAL_GRADIENT
        val view1 = findViewById<View>(R.id.gradient1)
        view1.background = gradient1

        val gradient2 = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, results)
        gradient2.gradientType = GradientDrawable.SWEEP_GRADIENT
        val view2 = findViewById<View>(R.id.gradient2)
        view2.background = gradient2
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.e("DuDuActivity", "onTouchEvent: ${event?.action}")
        return super.onTouchEvent(event)
    }

    fun testT() {
        val executor = Executors.newCachedThreadPool()
        for (i in 0..180) {
            executor.submit {
                sum(i)
            }
        }
    }

    private fun sum(n: Int) {
        var sum = 0
        for (i in 0..n) {
            sum += i
        }
        Log.i(TAG, "n = $n,sum = $sum in thread ${Thread.currentThread().name}")
    }
}

class MyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, style: Int = 0
) : AppCompatImageView(context, attrs, style) {

    private var datas: IntArray? = null
    var bitmap: Bitmap? = null


    private val screenDu: Triple<String, Int, Int> = Triple("frame.txt", 426, 266)
//    val screenDu: Triple<String, Int, Int> = Triple("screen_girl.json", 1200, 800)

    init {
        val json = AndroidFileUtils.getStringFromAssets(context, screenDu.first)
        val list = JSON.parseObject(json, object : TypeReference<List<Int>>() {})
        list?.let {

            datas = it.toIntArray()
        }
        datas?.let {
            val newData = IntArray(it.size * 4)
            var pos = 0
            it.forEachIndexed { index, color ->
                val alpha: Int = color shr 24 and 0xff
                val red: Int = color shr 16 and 0xff
                val green: Int = color shr 8 and 0xff
                val blue: Int = color and 0xff
                newData[pos] = red
                newData[pos + 1] = green
                newData[pos + 2] = blue
                newData[pos + 3] = alpha;
                pos += 4
            }

            AndroidFileUtils.saveFileToBox(context, newData.contentToString(), "11.txt")
        }


        datas?.let {
            Log.e("DuDuActivity", it.size.toString())
            bitmap = Bitmap.createBitmap(
                it, 0, screenDu.second, screenDu.second, screenDu.third, Bitmap.Config.ARGB_8888
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        datas?.let {
            val drawScaleX = 1f * width / screenDu.second
            val drawScaleY = 1f * height / screenDu.third
            Log.e("DuDuActivity", "scaleX=$drawScaleX,scaleY=$drawScaleY")
            canvas.scale(drawScaleX, drawScaleY)
            bitmap?.let { bp ->
                canvas.drawBitmap(bp, 0f, 0f, null)
            }
//            canvas.drawBitmap(it, 0, 426, 0f, 0f, 426, 266, false, null)
        }
    }

}