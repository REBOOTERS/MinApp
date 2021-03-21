package com.engineer.android.mini.ui.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engineer.android.mini.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        recyclerView = findViewById(R.id.recycler_view)
        val adapter = MyAdapter(datas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        recyclerView.adapter = adapter
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
            return MyHolder(view)
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            Log.e(
                "BindHolder",
                "onBindViewHolder() called with: holder = $holder, position = $position"
            )
            holder.title.text = holder.hashCode().toString()
            holder.index.text = position.toString()
        }

        override fun getItemCount(): Int {
            Log.e("getItemCount", "getItemCount() called")
            return datas.size
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