package com.engineer.android.mini.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.engineer.android.mini.MinApp
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.adapter.AlbumAdapter
import com.engineer.android.mini.ui.viewmodel.CursorQueryViewModel
import com.engineer.android.mini.util.SystemTools
import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.concurrent.thread

/**
 * Created on 2020/10/18.
 * @author rookie
 */
private const val TAG = "PictureBottomDialog"


@AndroidEntryPoint
class PictureBottomDialog : BaseBottomSheetDialog() {

    @Inject
    lateinit var imageList: ArrayList<Uri>

    @Inject
    lateinit var adapter: AlbumAdapter

    private val cursorQueryViewModel: CursorQueryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
        cursorQueryViewModel.loadImages()

        cursorQueryViewModel.videoResults.observe(this) {
            thread {
                Log.e(TAG, "currentThread ${Thread.currentThread().name}")
                it.forEach { uri ->
                    context?.let {
                        Log.e(
                            TAG, "uri=$uri," +
                                    "fileName=${SystemTools.getFileNameByUri(it, uri)}," +
                                    "path=${SystemTools.getVideoFilePathFromUri(it, uri)}"
                        )
                    }

                }
            }
        }
        cursorQueryViewModel.loadVideos()
    }


    private fun handleUri(it: List<Uri>) {
        thread {
            Log.e(TAG, "currentThread ${Thread.currentThread().name}")
            it.forEach { uri ->
                context?.let {
                    Log.e(
                        TAG, "uri=$uri," +
                                "fileName=${SystemTools.getFileNameByUri(it, uri)}," +
                                "path=${SystemTools.getVideoFilePathFromUri(it, uri)}"
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