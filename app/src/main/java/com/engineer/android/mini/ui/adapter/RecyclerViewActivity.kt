package com.engineer.android.mini.ui.adapter

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityRecyclerViewBinding
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import java.util.concurrent.atomic.AtomicInteger

class RecyclerViewActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private val datas by lazy {
        val list = ArrayList<String>()
        for (i in 0 until 20) {
            list.add(i.toString())
        }
        list
    }

    private lateinit var viewBinding: ActivityRecyclerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        recyclerView = findViewById(R.id.recycler_view)
        val adapter = MyAdapter(datas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(MyDecoration(this))
//        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

//        val linearSnapHelper = LinearSnapHelper()
//        linearSnapHelper.attachToRecyclerView(recyclerView)

//        val pagerSnapHelper = PagerSnapHelper()
//        pagerSnapHelper.attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    reflectValue(recyclerView)
                }
            }
        })

//        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            reflectValue(recyclerView)
//        }

        viewBinding.remove.setOnClickListener {
            datas.removeAt(0)
            adapter.notifyItemChanged(0)

        }
        viewBinding.add.setOnClickListener {
            datas.add(0, 0.toString())
            adapter.notifyItemChanged(0)

            viewBinding.add.post {
                println(1)
            }
            viewBinding.add.postDelayed({
                reflectValue(recyclerView)
            }, 300)
        }
    }

    private fun reflectValue(recyclerView: RecyclerView) {
        val rv = recyclerView.javaClass
        val recycler = rv.getDeclaredField("mRecycler")
        recycler.isAccessible = true
        val real = recycler.get(recyclerView)
        Log.e("reflect", "mRecycler: $real")

        val recyclerClazz = Class.forName("androidx.recyclerview.widget.RecyclerView\$Recycler")


        val mAttachedScrap = recyclerClazz.getDeclaredField("mAttachedScrap")
        mAttachedScrap.isAccessible = true
        val ccc = mAttachedScrap.get(real)
        Log.e("reflect", "mAttachedScrap: $ccc")

//        val mChangedScrap = recyclerClazz.getDeclaredField("mChangedScrap")
//        mChangedScrap.isAccessible = true
//        val cs = mChangedScrap.get(real)
//        Log.e("reflect", "mChangedScrap: $cs")

        val mCachedViews = recyclerClazz.getDeclaredField("mCachedViews")
        mCachedViews.isAccessible = true
        val cv = mCachedViews.get(real)
        Log.e("reflect", "mCachedViews: $cv")

        val mRecyclerPool = recyclerClazz.getDeclaredField("mRecyclerPool")
        mRecyclerPool.isAccessible = true
        val mp = mRecyclerPool.get(real)
        Log.e("reflect", "mRecyclerPool: $mp")
        if (mp is RecyclerView.RecycledViewPool) {
            Log.e(
                "reflect",
                "mRecyclerPool: ${mp.getRecycledView(0)},${mp.getRecycledViewCount(0)}"
            )

        }


        Log.e("reflect", "===============================================================")
    }

    class MyAdapter(private val datas: List<String>) : RecyclerView.Adapter<MyAdapter.MyHolder>() {

        private var createCount = AtomicInteger()

        inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.title_tv)
            val index: TextView = view.findViewById(R.id.index_tv)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            Log.e(
                "CreateHolder",
                "onCreateViewHolder() called with: viewType = $viewType, createCount=${createCount.incrementAndGet()}"
            )
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            val holder = MyHolder(view)

            view.setOnClickListener {
                val adapterPosition = holder.adapterPosition
                val layoutPosition = holder.layoutPosition
                "adapterPosition = $adapterPosition\n layoutPosition = $layoutPosition".toast()
            }
            return holder
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            Log.e(
                "BindHolder",
                "onBindViewHolder() called with: holder = $holder, position = $position"
            )
            holder.title.text = ('A' + position).toString()
            holder.index.text = datas[position]
        }

        override fun getItemCount(): Int {
            Log.e("getItemCount", "getItemCount() called")
            return datas.size
        }

        override fun onViewRecycled(holder: MyHolder) {
            super.onViewRecycled(holder)
            val view = holder.itemView.findViewById<TextView>(R.id.title_tv)
            val s = view.text.toString()
            Log.d("onViewRecycled", "onViewRecycled() called with: holder = $holder , s=$s")

        }

        override fun onViewAttachedToWindow(holder: MyHolder) {
            super.onViewAttachedToWindow(holder)
            Log.e("ach", "onViewAttachedToWindow() called with: holder = $holder")
        }

        override fun onViewDetachedFromWindow(holder: MyHolder) {
            super.onViewDetachedFromWindow(holder)
            Log.e("ach", "onViewDetachedFromWindow() called with: holder = $holder")
        }
    }
}

class MyDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private var decorationHeight = 0
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmapTag: Bitmap = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.phone
    )

    init {
        paint.color = Color.RED
    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val top = child.top + child.height / 2f
            val left = child.width / 2f
            c.drawBitmap(bitmapTag, left, top, paint)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val index = parent.getChildAdapterPosition(child)
            if (index == 0) {
                continue
            }
            val top = child.top - decorationHeight
            val left = parent.paddingLeft
            val bottom = child.top
            val right = parent.width - parent.paddingRight
            val rect = Rect(left, top, right, bottom)
            c.drawRect(rect, paint)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = 5.dp
            decorationHeight = 5.dp
        }
    }
}