package com.engineer.android.mini.ui.tabs.ui.fragments

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

}