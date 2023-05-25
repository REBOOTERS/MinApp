package com.engineer.android.mini.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.adapter.GifAdapter
import com.engineer.android.mini.ui.adapter.GifAdapterAnnotation
import com.engineer.android.mini.ui.viewmodel.CursorQueryViewModel
import com.engineer.common.utils.SystemTools
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.concurrent.thread

@AndroidEntryPoint
open class GifBottomDialog() : BaseBottomSheetDialog() {
    private val TAG = "GifBottomDialog"

    @Inject
    lateinit var imageList: ArrayList<Uri>

    @GifAdapterAnnotation
    @Inject
    lateinit var adapter: GifAdapter

    private val cursorQueryViewModel: CursorQueryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val columns = 3
        recyclerView.layoutManager = GridLayoutManager(context, columns)
        recyclerView.adapter = adapter

        cursorQueryViewModel.imageResults.observe(this) {
            it.size.toString().toast()
            imageList.clear()
            imageList.addAll(it)
            adapter.notifyDataSetChanged()
            handleUri(it)
        }

        cursorQueryViewModel.loadGifs()
    }


    private fun handleUri(it: List<Uri>) {
        thread {
            Log.e(TAG, "currentThread ${Thread.currentThread().name}")
            it.forEach { uri ->
                context?.let {
                    Log.e(
                        TAG, "uri=$uri," + "fileName=${
                            SystemTools.getFileNameByUri(
                                it, uri
                            )
                        }," + "path=${SystemTools.getVideoFilePathFromUri(it, uri)}"
                    )
//                    val fd = it.contentResolver.openFileDescriptor(uri, "r")
//                    fd?.let {
//                        Log.e(TAG, "handleUri: $fd")
//                    }
                }
            }
        }
    }

    override fun getHeight(): Int {
        return super.getHeight() * 2 / 3
    }
}