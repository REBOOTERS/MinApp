package com.engineer.android.mini.ui.adapter

import android.database.Observable
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.HandlerCompat
import androidx.recyclerview.widget.*
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_recycler_view.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        recyclerView = findViewById(R.id.recycler_view)
        val adapter = MyAdapter(datas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        val linearSnapHelper = LinearSnapHelper()
//        linearSnapHelper.attachToRecyclerView(recyclerView)

        val pagerSnapHelper = PagerSnapHelper()
//        pagerSnapHelper.attachToRecyclerView(recyclerView)

        recyclerView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            reflectValue(recyclerView)
        }

        remove.setOnClickListener {
            datas.removeAt(0)
            adapter.notifyItemChanged(0)
        }
        add.setOnClickListener {
            datas.add(0, 0.toString())
            adapter.notifyItemChanged(0)

            add.post {
                println(1)
            }
            add.postDelayed({}, 0)
        }

        val h = HandlerCompat.createAsync(Looper.getMainLooper())
    }

    private fun reflectValue(recyclerView: RecyclerView) {
        val rv = recyclerView.javaClass
        val recycler = rv.getDeclaredField("mRecycler")
        recycler.isAccessible = true

        Log.e("reflect", "recycler: $recycler")

        val real = recycler.get(recyclerView)

        Log.e("reflect", "real: $real")

        val recyclerClazz = Class.forName("androidx.recyclerview.widget.RecyclerView\$Recycler")


        val mAttachedScrap = recyclerClazz.getDeclaredField("mAttachedScrap")
        mAttachedScrap.isAccessible = true
        Log.e("reflect", "mm: $mAttachedScrap")
        val ccc = mAttachedScrap.get(real)
        Log.e("reflect", "as: $ccc")

        val mChangedScrap = recyclerClazz.getDeclaredField("mChangedScrap")
        mChangedScrap.isAccessible = true
        val cs = mChangedScrap.get(real)
        Log.e("reflect", "cs: $cs")

        val mCachedViews = recyclerClazz.getDeclaredField("mCachedViews")
        mCachedViews.isAccessible = true
        val cd = mCachedViews.get(real)
        Log.e("reflect", "cd: $cd")

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
            holder.title.text = holder.hashCode().toString()
            holder.index.text = datas[position]
        }

        override fun getItemCount(): Int {
            Log.e("getItemCount", "getItemCount() called")
            return datas.size
        }

        override fun onViewRecycled(holder: MyHolder) {
            super.onViewRecycled(holder)
            Log.d("onViewRecycled", "onViewRecycled() called with: holder = $holder")
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


    private abstract class MyObserver {
        abstract fun update(str: String)
    }

    private class MyObservable : Observable<MyObserver>() {
        fun notifyDataSetChanged() {
            mObservers.forEach {
                it.update("aaa")
            }
        }
    }


}