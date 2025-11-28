package com.engineer.mvp.mini.focus

import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.engineer.mvp.mini.R
import com.engineer.mvp.mini.databinding.PageListBinding

class RecyclerViewPage : AppCompatActivity() {
    private val TAG = "RecyclerViewPage"

    private lateinit var vBinding: PageListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vBinding = PageListBinding.inflate(layoutInflater)
        setContentView(vBinding.root)
        renderRecyclerView(vBinding.recyclerView)
    }


    private fun renderRecyclerView(recyclerView: RecyclerView) {
        val doc = getString(R.string.long_content)

        val dataList = ArrayList<Item>()
        val headerItem = HeadItem()
        headerItem.info = doc
        dataList.add(headerItem)

        for (i in 0..5) {
            val contentItem = ContentItem()
            contentItem.info = "项目 ${i + 1} $doc"
            dataList.add(contentItem)
        }

        val mixItem = MixItem()
        val subList = ArrayList<ContentItem>()
        for (i in 0..10) {
            val contentItem = ContentItem()
            contentItem.info = "sub 项目 ${i + 1} $doc"
            subList.add(contentItem)
            mixItem.subList = subList
        }
        dataList.add(mixItem)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SimpleAdapter(dataList)
        recyclerView.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(@NonNull view: View) {
                view.onFocusChangeListener =
                    View.OnFocusChangeListener { v: View?, hasFocus: Boolean ->
                        if (hasFocus) {
                            val position = recyclerView.getChildAdapterPosition(v!!)
                            if (position == 1) { // 第二个 item（header 是第一个）
                                // 延迟滚动确保焦点变化完成
                                recyclerView.post(Runnable { recyclerView.smoothScrollToPosition(0) })
                            }
                        }
                    }
            }

            override fun onChildViewDetachedFromWindow(@NonNull view: View) {
                view.onFocusChangeListener = null
            }
        })
    }


    class SimpleAdapter(private val data: List<Item>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val TAG = "RecyclerViewPage"

        private val imgRes = arrayOf(R.drawable.s1, R.drawable.s2, R.drawable.s3)
        private val len = imgRes.size

        class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.content)
            val imageSwitcher: ImageView = view.findViewById(R.id.image)

            init {
                view.setStateListAnimator(
                    AnimatorInflater.loadStateListAnimator(
                        view.context, R.drawable.common_selector
                    )
                )
            }
        }

        class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.content)
        }

        class MixViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val subRecyclerView: RecyclerView = view.findViewById(R.id.sub_recyclerView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            if (viewType == HEADER) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_unfocusable, parent, false)
                return HeaderViewHolder(view)
            } else if (viewType == MIX) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_sub_list, parent, false)
                return MixViewHolder(view)
            }
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_focusable, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is HeaderViewHolder) {
                holder.textView.text = data[position].getContent()
            } else if (holder is ItemViewHolder) {
                holder.textView.text = data[position].getContent()
                val id = imgRes[position % len]
                holder.imageSwitcher.setImageResource(id)
            } else if (holder is MixViewHolder) {
                val model: MixItem = data[position] as MixItem

                val adapter = SimpleAdapter(model.subList)
                holder.subRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 3)
//                holder.subRecyclerView.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL)
                holder.subRecyclerView.adapter = adapter

            }
        }

        override fun getItemViewType(position: Int): Int {
            return data[position].getType()
        }

        override fun getItemCount() = data.size
    }


    interface Item {
        fun getType(): Int

        fun getContent(): String
    }

    class HeadItem : Item {
        var info = ""
        override fun getType(): Int {
            return HEADER
        }

        override fun getContent(): String {
            return info
        }
    }

    class ContentItem : Item {
        var info = ""
        override fun getType(): Int {
            return CONTENT
        }

        override fun getContent(): String {
            return info
        }

    }

    class MixItem : Item {

        var subList: ArrayList<ContentItem> = ArrayList()

        override fun getType(): Int {
            return MIX
        }

        override fun getContent(): String {
            return ""
        }

    }

    companion object {
        const val HEADER = 0
        const val CONTENT = 1

        const val MIX = 2
    }
}