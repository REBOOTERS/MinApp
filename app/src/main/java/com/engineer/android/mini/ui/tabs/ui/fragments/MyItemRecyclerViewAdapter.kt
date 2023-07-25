package com.engineer.android.mini.ui.tabs.ui.fragments

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.engineer.android.mini.R

import com.engineer.android.mini.ui.tabs.ui.fragments.placeholder.PlaceholderContent.PlaceholderItem
import com.engineer.android.mini.databinding.FragmentItemBinding
import com.engineer.android.mini.ui.tabs.ListViewModel

class MyItemRecyclerViewAdapter(
    private val listViewModel: ListViewModel,
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {
    private val TAG = "MyItemRecyclerViewAdapt"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.binding.listViewModel = listViewModel
        holder.binding.item = item
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        Log.d(TAG, "onAttachedToRecyclerView() called with: recyclerView = $recyclerView")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        Log.d(TAG, "onDetachedFromRecyclerView() called with: recyclerView = $recyclerView")
    }

}