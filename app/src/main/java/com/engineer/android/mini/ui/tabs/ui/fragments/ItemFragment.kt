package com.engineer.android.mini.ui.tabs.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engineer.android.mini.R
import com.engineer.android.mini.ui.adapter.MyDecoration
import com.engineer.android.mini.ui.tabs.ListViewModel

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {

    private var columnCount = 2

    private lateinit var listViewModel: ListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        // Set the adapter
        if (recyclerView is RecyclerView) {
            with(recyclerView) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
            }
//            context?.let {
//                recyclerView.addItemDecoration(
//                    DividerItemDecoration(it, DividerItemDecoration.VERTICAL)
//                )
//            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listViewModel = ListViewModel(requireActivity().application)


        listViewModel.items.observe(requireActivity()) {
            val recyclerView = view.findViewById<RecyclerView>(R.id.list)
            if (recyclerView is RecyclerView) {
                recyclerView.adapter = MyItemRecyclerViewAdapter(listViewModel, it.ITEMS)
            }
        }
        listViewModel.count.observe(requireActivity()) {
            val countTv = view.findViewById<TextView>(R.id.list_count)
            countTv.text = it.toString()
        }
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"


        @JvmStatic
        fun newInstance(columnCount: Int) = ItemFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_COLUMN_COUNT, columnCount)
            }
        }
    }
}