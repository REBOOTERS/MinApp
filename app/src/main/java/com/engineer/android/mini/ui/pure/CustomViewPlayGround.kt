package com.engineer.android.mini.ui.pure

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.NinePatchDrawable
import android.os.Bundle
import android.text.Editable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityCustomViewBinding
import com.engineer.android.mini.ext.*
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.adapter.TextWatcherSimpleAdapter
import com.engineer.android.mini.ui.pure.helper.MeasureSpecCopy
import com.engineer.android.mini.util.ImagePool
import com.engineer.android.mini.util.KeyBoardUtil
import com.engineer.common.utils.SystemTools
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.internal.TextWatcherAdapter
import kotlin.random.Random


/**
 * Created on 2020/12/29.
 * @author rookie
 */
class SimpleViewOne @JvmOverloads
constructor(
    context: Context, attributeSet:
    AttributeSet? = null, style: Int = 0
) : View(context, attributeSet, style) {

    private val paint: Paint = Paint()

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 5.dp.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.let {
//            canvas.translate(width / 2.0f, height / 2.0f)
            it.drawColor(Color.CYAN)
            val radius = width.coerceAtMost(height) / 2f - 5.dp
            it.drawCircle(width / 2.0f, height / 2.0f, radius, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(
            "SimpleViewOne",
            "w = ${MeasureSpec.toString(widthMeasureSpec)},h = ${
                MeasureSpec.toString(heightMeasureSpec)
            }"
        )
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        var wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        var hSize = MeasureSpec.getSize(heightMeasureSpec)


        wSize = resolveSize(210, widthMeasureSpec)
        hSize = resolveSize(210, heightMeasureSpec)


        Log.e(
            "SimpleViewOne",
            "w1 = ${MeasureSpec.toString(widthMeasureSpec)},h1 = ${
                MeasureSpec.toString(heightMeasureSpec)
            }"
        )
        Log.e("SimpleViewOne", "===============")
        setMeasuredDimension(wSize, hSize)
//        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec)
        SystemTools.printMethodTrace("SimpleViewOne")
    }
}

class SquareImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null, style: Int = 0
) : AppCompatImageView(context, attributeSet, style) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = Color.WHITE
        paint.textSize = 10.sp
    }

    private var textContent = ""

    fun setTextContext(content: String) {
        val sb = StringBuilder()
        sb.append("width=$width").append(" height=$height").append("\n")
        sb.append(content)
        textContent = sb.toString()

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.drawText(textContent, 0f, 20f, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(
            "SquareImageView",
            "onMeasure() called with: widthMeasureSpec = $widthMeasureSpec, heightMeasureSpec = $heightMeasureSpec"
        )
        if (drawable != null) {

            Log.e("SquareImageView", "drawable =${drawable.javaClass.name}")
            Log.e("SquareImageView", "drawable =${drawable.bounds}")
        }

        Log.e("SquareImageView", "widthMeasureSpec  = ${MeasureSpec.toString(widthMeasureSpec)}")
        Log.e("SquareImageView", "heightMeasureSpec = ${MeasureSpec.toString(heightMeasureSpec)}")

        Log.e("SquareImageView", "w = $measuredWidth")
        Log.e("SquareImageView", "h = $measuredHeight")

        val wh = measuredWidth.coerceAtMost(measuredHeight)
        setMeasuredDimension(wh,wh)
        Log.e("SquareImageView", "===============")
    }
}

class LogLinearLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null, style: Int = 0
) : LinearLayout(context, attributeSet, style) {

    fun test() {
        requestLayout()
        invalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.e(
            "LogLinearLayout",
            "onLayout() called with: changed = $changed, l = $l, t = $t, r = $r, b = $b"
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(
            "LogLinearLayout",
            "w = ${MeasureSpec.toString(widthMeasureSpec)},h = ${
                MeasureSpec.toString(heightMeasureSpec)
            }"
        )
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        Log.e("LogLinearLayout", "dispatchDraw() called with: canvas = $canvas")
    }
}

class FirstViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null, style: Int = 0
) : ViewGroup(context, attributeSet, style) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        setWillNotDraw(false)
        paint.textSize = 12.sp
        paint.color = Color.RED
        paint.textAlign = Paint.Align.CENTER
        Log.e("FirstViewGroup", "parent is $parent")
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.let {
            it.drawText("FirstViewGroup", width / 2f, height / 2f, paint)
            it.drawText("FirstViewGroup", width / 2f, 25f, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(
            "FirstViewGroup",
            "w = ${MeasureSpec.toString(widthMeasureSpec)},h = ${
                MeasureSpec.toString(heightMeasureSpec)
            }，childCount= $childCount"
        )
        var selfWidth = 0
        var selfHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as MarginLayoutParams
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val w = child.measuredWidth
            val h = child.measuredHeight
            Log.e(
                "FirstViewGroup",
                "onMeasure() called with: w=$w,h=$h"
            )
            selfWidth = selfWidth.coerceAtLeast(w + lp.leftMargin + lp.rightMargin)
            selfHeight += h + lp.topMargin + lp.bottomMargin
        }
        val width = resolveSize(selfWidth, widthMeasureSpec)
        val height = resolveSize(selfHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        hackInfo()
        Log.e(
            "FirstViewGroup",
            "onLayout() called with: changed = $changed, l = $l, t = $t, r = $r, b = $b"
        )
        val count = childCount
        var childTop = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            val lp = child.layoutParams as MarginLayoutParams
            val left = lp.leftMargin
            val top = childTop + lp.topMargin
            val right = left + child.measuredWidth
            val bottom = top + child.measuredHeight
            Log.e(
                "FirstViewGroup",
                "onLayout: i = $i child[$i] w=${child.measuredWidth},h=${child.measuredHeight}"
            )

//            child.layout(0, childTop, child.measuredWidth, childTop + child.measuredHeight)
            child.layout(left, top, right, bottom)
            childTop += childTop + child.measuredHeight + lp.topMargin + lp.bottomMargin
        }
    }

    private fun hackInfo(): Pair<Int, Int> {
        var w = 0
        var h = 0
        try {
            val clazz = this.javaClass
            val getViewRootImpl = clazz.getMethod("getViewRootImpl")
            val result = getViewRootImpl.invoke(parent.parent)
            val mWidth = result.javaClass.getDeclaredField("mWidth")
            val mHeight = result.javaClass.getDeclaredField("mHeight")
            mWidth.isAccessible = true
            mHeight.isAccessible = true
            w = mWidth.get(result) as Int
            h = mHeight.get(result) as Int

            Log.e("hackInfo", "w=$w,h=$h")
        } catch (e: Exception) {
        }
        return Pair(w, h)
    }

}

class MyFlowLayout @JvmOverloads
constructor(context: Context, attributeSet: AttributeSet? = null, style: Int = 0) :
    ViewGroup(context, attributeSet, style) {


    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        super.generateLayoutParams(p)
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //获得宽高的测量模式和测量值
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var perLineWidth = 0 // 每一行总宽度
        var perLineMaxH = 0 // 每一行最多高度
        var maxH = 0 // 当前 ViewGroup 总高度

        for (i in 0 until childCount) {
            val childView: View = getChildAt(i)

            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            val marginLp = childView.layoutParams as MarginLayoutParams
            val childWidth = childView.measuredWidth + marginLp.leftMargin + marginLp.rightMargin
            val childHeight = childView.measuredHeight + marginLp.topMargin + marginLp.bottomMargin

            // 需要换行
            if (perLineWidth + childWidth > widthSize) {
                // 高度累加
                maxH += perLineMaxH
                // 开始新的一行
                perLineWidth = childWidth
                perLineMaxH = childHeight
            } else { // 不需要换行
                // 行宽度累加
                perLineWidth += childWidth
                // 当前行最大高度
                perLineMaxH = Math.max(perLineMaxH, childHeight)
            }
            //当该View已是最后一个View时，将该行最大高度添加到totalHeight中
            if (i == childCount - 1) {
                maxH += perLineMaxH
            }
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = maxH
        }
        Log.e(
            "MyFlowLayout",
            "onMeasure() called with: w=$widthSize,h=$heightSize"
        )
        setMeasuredDimension(widthSize, heightSize)
    }

    //存放容器中所有的View
    private val mAllViews: ArrayList<List<View>> = ArrayList()

    //存放每一行最高View的高度
    private val mPerLineMaxHeight: ArrayList<Int> = ArrayList()

    //每一行存放的 view
    private var mLineViews = ArrayList<View>()

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mAllViews.clear()
        mPerLineMaxHeight.clear()

        var lineWidth = 0 // 每一行已经占用的宽度
        var lineMaxH = 0  // 每一行最大的高度

        for (i in 0 until childCount) {
            Log.e("MyFlowLayout", "i=$i")
            val childView = getChildAt(i)
            val marginLp = childView.layoutParams as MarginLayoutParams
            val childWidth = childView.measuredWidth + marginLp.leftMargin + marginLp.rightMargin
            val childHeight = childView.measuredHeight + marginLp.topMargin + marginLp.bottomMargin

            if (lineWidth + childWidth > width) { // 累计宽度超过一行的宽度
                // 已累计的一行添加到列表中
                mAllViews.add(mLineViews)
                mPerLineMaxHeight.add(lineMaxH)
                // 重置累计变量
                lineWidth = 0
                lineMaxH = 0
                mLineViews = ArrayList()
            }
            // 在同一行中的元素做累计
            lineWidth += childWidth
            lineMaxH = lineMaxH.coerceAtLeast(childHeight)
            mLineViews.add(childView)
        }
        // 最后一行特殊处理
        mAllViews.add(mLineViews)
        mPerLineMaxHeight.add(lineMaxH)
        Log.e("MyFlowLayout", "allViews ${mAllViews}")
        Log.e("MyFlowLayout", "allHight ${mPerLineMaxHeight.size}")
        // 遍历集合中的 view
        var childLeft = 0
        var childTop = 0
        for (i in 0 until mAllViews.size) {
            val perLineViews = mAllViews[i] // 每一行的所有 views
            val perLineH = mPerLineMaxHeight[i] // 每一行的最大高度
            for (j in perLineViews.indices) {
                val childView = perLineViews[j]
                if (childView is TextView) {
                    Log.e("MyFlowLayout", "value is ${childView.text}")
                }
                val lp = childView.layoutParams as MarginLayoutParams
                val l = childLeft + lp.leftMargin
                val t = childTop + lp.topMargin
                val r = l + childView.measuredWidth
                val b = t + childView.measuredHeight
                Log.e("MyFlowLayout", "l=$l,t=$t,r=$r,b=$b")
                childView.layout(l, t, r, b)
                childLeft += lp.leftMargin + childView.measuredWidth + lp.rightMargin
            }
            childLeft = 0
            childTop += perLineH
        }
    }
}


class CustomViewActivity : BaseActivity() {

    companion object {
        // make a memory leak
        private var maybeLeakView: View? = null
    }

    private lateinit var viewBinding: ActivityCustomViewBinding

    private val list = ArrayList<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCustomViewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        maybeLeakView = viewBinding.simpleView
        viewBinding.simpleView.setOnClickListener {
            for (i in 0..10000) {
                list.add(ImageView(this))
            }
            list.size.toString().toast()
        }
        viewBinding.squareIv.setOnClickListener {
            val resId = ImagePool.next()
            val resStr = resources.getResourceName(resId)
            "$resId show $resStr".toast()
            viewBinding.squareIv.setImageResource(resId)
            viewBinding.squareIv.setTextContext(resStr)
        }
        viewBinding.squareIv.setImageResource(ImagePool.next())

        KeyBoardUtil.checkVisible(viewBinding.et) {
            if (it) {
                val parentView = viewBinding.et.parent as ViewGroup
                parentView.setOnClickListener {
                    KeyBoardUtil.hideKeyboard(viewBinding.et)
                }
            } else {
                val parentView = viewBinding.et.parent as ViewGroup
                parentView.setOnClickListener(null)
            }
        }

        val bitmap = BitmapFactory.decodeFile("sdcard/chatfrom_bg_normal.9.png")
        if (bitmap != null) {
            val chunk = bitmap.ninePatchChunk
            if (NinePatch.isNinePatchChunk(chunk)) {
                val drawable = NinePatchDrawable(resources, bitmap, chunk, Rect(), null)

                Log.e(TAG, "drawable is $drawable")
            }
        }

        viewBinding.et.addTextChangedListener(object : TextWatcherSimpleAdapter() {
            override fun afterTextChanged(s: Editable?) {
                viewBinding.content.text = s.toString()
            }
        })
//        viewBinding.et.setSelection(2)
        viewBinding.content.maxWidth = screenWidth - 92.dp

        setupFlexBox()

        MeasureSpecCopy.print()

        var done = false
        viewBinding.real.setOnClickListener {
            if (done) {
                viewBinding.real.visibility = View.GONE
            }
            viewBinding.fake.post {
                val w = viewBinding.fake.width
                val h = viewBinding.fake.height
                Log.e(TAG, "w=$w,h=$h")
                Log.e(TAG, "w1=${viewBinding.real.width},h1=${viewBinding.real.height}")
                val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                viewBinding.fake.draw(canvas)
                viewBinding.real.setImageBitmap(bitmap)
                done = true
            }
        }

        var msg: Class<out Any>? = lifecycle::class.java
        while (msg != null) {
            Log.d(TAG, "onCreate() called with: ${msg.name}")
            msg = msg.superclass
        }

    }

    private fun setupFlexBox() {
        var tv: TextView? = null
        val random = Random(10000)
        for (i in 0..10) {
            tv = TextView(this)

            tv.setPadding(8.dp, 4.dp, 8.dp, 4.dp)
            tv.setBackgroundResource(R.drawable.tv_tag_bg)
            tv.text = random.nextInt().toString()
            val p = FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            p.setMargins(10.dp, 10.dp, 10.dp, 10.dp)
            viewBinding.flexbox.addView(tv, p)
        }
        // 用一种 hack 的方式给 FlexboxLayout 添加分割线
        viewBinding.specialLine.post {
            val parmas = viewBinding.specialLine.layoutParams as ConstraintLayout.LayoutParams
            val viewHeight = tv?.measuredHeight ?: 0
            parmas.bottomMargin = 20.dp + viewHeight
            viewBinding.specialLine.layoutParams = parmas
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        maybeLeakView = null
    }
}

class WrapContentActivity : BaseActivity() {

    override fun onStart() {
        super.onStart()
        Log.d("LogLinearLayout", "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LogLinearLayout", "onResume() called")
        SystemTools.printMethodTrace("onResume")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            traversalPrintViewTree(window.decorView)
        }
    }

    private fun traversalPrintViewTree(view: View?, space: Int = 0) {
        if (view == null) {
            return
        }
        val sb = StringBuilder()
        sb.append("|")
        repeat(space) {
            sb.append("__")
        }
        sb.append(
            " ${view.javaClass.name} w:${view.width} h:${view.height} left:${view.left}," +
                    "top:${view.top},right:${view.right},bottom:${view.bottom}"
        )
        Log.e("ViewTree", sb.toString())
        if (view is ViewGroup) {
            val step = space + 1
            for (i in 0 until view.childCount) {
                traversalPrintViewTree(view.getChildAt(i), step)
            }
        }
    }

    private lateinit var simpleViewOne: SimpleViewOne

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrap_content)
        simpleViewOne = findViewById(R.id.one_list)
        SystemTools.printMethodTrace("onCreate")
        Log.e("LogLinearLayout", "onCreate() called with: savedInstanceState = $savedInstanceState")

        Log.e("displayMetrics", "density       = ${Resources.getSystem().displayMetrics.density}")
        Log.e(
            "displayMetrics",
            "densityDpi    = ${Resources.getSystem().displayMetrics.densityDpi}"
        )
        Log.e(
            "displayMetrics",
            "scaledDensity = ${Resources.getSystem().displayMetrics.scaledDensity}"
        )
        Log.e("displayMetrics", "xdpi          = ${Resources.getSystem().displayMetrics.xdpi}")
        Log.e("displayMetrics", "ydpi          = ${Resources.getSystem().displayMetrics.ydpi}")
        Log.e(
            "displayMetrics",
            "widthPixels   = ${Resources.getSystem().displayMetrics.widthPixels}"
        )
        Log.e(
            "displayMetrics",
            "heightPixels  = ${Resources.getSystem().displayMetrics.heightPixels}"
        )
        Log.e("displayMetrics", "displayMetrics= ${Resources.getSystem().displayMetrics}")

        Log.e("displayMetrics", "screenW = ${screenWidth}, screenH = $screenHeight")
        Log.e("displayMetrics", "screenW = ${screenWidth.px}, screenH = ${screenHeight.px}")
    }
}