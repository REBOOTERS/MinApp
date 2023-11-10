package com.engineer.android.mini.ui.pure

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.engineer.android.mini.R
import com.engineer.common.utils.AndroidFileUtils


class DuDuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_du_du)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.e("DuDuActivity", "onTouchEvent: ${event?.action}")
        return super.onTouchEvent(event)
    }
}

class MyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, style: Int = 0
) : AppCompatImageView(context, attrs, style) {

    private var datas: IntArray? = null
    var bitmap: Bitmap? = null


    val screenDu: Triple<String, Int, Int> = Triple("frame.txt", 426, 266)
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

            AndroidFileUtils.saveFileToBox(context,newData.contentToString(),"11.txt")
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