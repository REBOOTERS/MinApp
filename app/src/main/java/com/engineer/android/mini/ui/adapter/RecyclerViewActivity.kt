package com.engineer.android.mini.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityRecyclerViewBinding
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.viewmodel.RecyclerViewModel
import com.engineer.android.mini.util.BindingClosure
import com.engineer.android.mini.util.bind
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class RecyclerViewActivity : BaseActivity(), OnRefreshLoadMoreListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerViewModel: RecyclerViewModel
    private val adapter = MyAdapter()

    private lateinit var viewBinding: ActivityRecyclerViewBinding
    private var loadMoreCount = 0
    private var targetPos = 2

    private var globalPos = 0
    private var globalOffset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        recyclerViewModel = ViewModelProvider(this)[RecyclerViewModel::class.java]
        recyclerView = findViewById(R.id.recycler_view)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
//        recyclerView.addItemDecoration(MyDecoration(this))
//        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        recyclerViewModel.datas.observe(this) {
            adapter.updateDatas(it)
            viewBinding.refreshLayout.finishRefresh(0)

            viewBinding.seekbar.max = adapter.itemCount - 1
        }
        val datas: ArrayList<String> = ArrayList()

        recyclerView.bind(datas, R.layout.list_item_avatar, object : (View, String, Int) -> Unit {
            override fun invoke(p1: View, item: String, position: Int) {
                val title: TextView = p1.findViewById(R.id.title_tv)
                val index: TextView = p1.findViewById(R.id.index_tv)

                title.text = item
                index.text = position.toString()
            }
        })

        recyclerView.bind(datas).map(R.layout.list_item, object : (String, Int) -> Boolean {
                override fun invoke(p1: String, p2: Int): Boolean {
                    return p2 < 5
                }
            }, object : (View, String, Int) -> Unit {
                override fun invoke(p1: View, p2: String, p3: Int) {
                    // view 和数据绑定
                }
            }).map(R.layout.list_item_avatar, object : (String, Int) -> Boolean {
                override fun invoke(p1: String, p2: Int): Boolean {
                    return p2 >= 5
                }
            }, object : (View, String, Int) -> Unit {
                override fun invoke(p1: View, p2: String, p3: Int) {
                    // view 和数据绑定
                }
            })

//        val linearSnapHelper = LinearSnapHelper()
//        linearSnapHelper.attachToRecyclerView(recyclerView)

//        val pagerSnapHelper = PagerSnapHelper()
//        pagerSnapHelper.attachToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    reflectValue(recyclerView)

                    val p1 = layoutManager.findLastVisibleItemPosition()
                    val p2 = layoutManager.findLastCompletelyVisibleItemPosition()
                    val count = adapter.itemCount

                    val info = String.format(
                        Locale.getDefault(),
                        "LastVisiblePos = %d,LastCompletelyVisiblePos = %d,count = %d",
                        p1,
                        p2,
                        count
                    )
//                    Log.i(TAG, info)
                    val p3 = layoutManager.findFirstVisibleItemPosition()
                    val view = layoutManager.findViewByPosition(p3)
                    Log.i(TAG, "findFirstVisibleItemPosition $p3, top ${view?.top},${view?.height}")
                }
            }
        })


//        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            reflectValue(recyclerView)
//        }

        viewBinding.remove.setOnClickListener {
            adapter.removeData(0)

        }
        viewBinding.add.setOnClickListener {
            adapter.addData("new item")
            viewBinding.recyclerView.scrollToPosition(adapter.itemCount - 1)

            viewBinding.add.post {
                println(1)
            }
            viewBinding.add.postDelayed({
                reflectValue(recyclerView)
            }, 300)
        }
        viewBinding.fixPos.setOnClickListener {
            targetPos += 2
            targetPos %= adapter.itemCount
            "to $targetPos".toast()
            recyclerView.scrollToPosition(targetPos)
        }
        viewBinding.movePos.setOnClickListener {
            targetPos -= 1
            if (targetPos < 0) {
                targetPos = 0
            }
            "move to $targetPos".toast()
            recyclerView.scrollToPosition(targetPos)
            layoutManager.scrollToPositionWithOffset(targetPos, 0)
        }
        viewBinding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "seekbar Change $progress")
                globalPos = progress
                globalMove()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })


        viewBinding.seekbar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "seekbar2 Change $progress")
                globalOffset = progress
                globalMove()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        viewBinding.seekbar2.max = 100.dp

        viewBinding.refreshLayout.setOnRefreshLoadMoreListener(this)

//        viewBinding.refreshLayout.autoRefreshAnimationOnly()
        recyclerViewModel.loadData()

        handleScroll()
    }

    private fun globalMove() {
        Log.i(TAG, "GlobalMove $globalPos $globalOffset")
        recyclerView.scrollToPosition(globalPos)
        layoutManager.scrollToPositionWithOffset(globalPos, globalOffset)
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
                "reflect", "mRecyclerPool: ${mp.getRecycledView(0)},${mp.getRecycledViewCount(0)}"
            )

        }


        Log.e("reflect", "===============================================================")
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        Log.d(TAG, "onRefresh() called with: refreshLayout = $refreshLayout")
        recyclerViewModel.loadData()
        loadMoreCount = 0
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        Log.d(TAG, "onLoadMore() called with: refreshLayout = $refreshLayout")
        if (loadMoreCount < 3) {
            viewBinding.refreshLayout.finishLoadMore(2000)
            loadMoreCount++
        } else {
            viewBinding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    private fun handleScroll() {
        val observer = object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                Log.i(TAG, "onChanged")
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                Log.i(TAG, "onItemRangeChanged $positionStart $itemCount")

            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Log.i(TAG, "onItemRangeInserted $positionStart $itemCount")

            }
        }
        adapter.registerAdapterDataObserver(observer)
    }
}

class MyAdapter : RecyclerView.Adapter<MyAdapter.MyHolder>() {

    private var createCount = AtomicInteger()
    private val datas: ArrayList<String> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun updateDatas(inputs: List<String>) {
        datas.clear()
        datas.addAll(inputs)
        notifyDataSetChanged()
    }

    fun addData(input: String) {
        val inputs = arrayListOf(input)
        addDatas(inputs)
    }

    fun removeData(index: Int) {
        if (index <= datas.size) {
            return
        }
        datas.removeAt(index)
        notifyItemRemoved(index)
    }

    fun addDatas(inputs: List<String>) {
        datas.addAll(inputs)
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return datas.size - inputs.size
            }

            override fun getNewListSize(): Int {
                return inputs.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return datas[oldItemPosition] == inputs[newItemPosition]
            }

            override fun areContentsTheSame(
                oldItemPosition: Int, newItemPosition: Int
            ): Boolean {
                return datas[oldItemPosition] == inputs[newItemPosition]
            }
        })
        result.dispatchUpdatesTo(this)
    }

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
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_avatar, parent, false)
        val holder = MyHolder(view)

        view.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            val layoutPosition = holder.layoutPosition
            "adapterPosition = $adapterPosition\n layoutPosition = $layoutPosition".toast()
        }
        return holder
    }

    val str =
        "ABI是应用程序与处理器指令集之间的接口，不同的ABI对应不同的CPU架构。例如，armeabi-v7a支持ARMv7架构的设备，arm64-v8a支持64位ARM架构的设备，x86支持x86架构的设备，等等"

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        Log.e(
            "BindHolder", "onBindViewHolder() called with: holder = $holder, position = $position"
        )
        if (position == 12) {
            holder.title.text = str + str + str + str + str
        } else {
            holder.title.text = str.substring(Random(position).nextInt(str.length))
        }
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

class MyDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private var decorationHeight = 0
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmapTag: Bitmap = BitmapFactory.decodeResource(
        context.resources, R.drawable.phone
    )

    init {
        paint.color = Color.RED
    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val top = child.top + 0f
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
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = 5.dp
            decorationHeight = 5.dp
        }
    }
}