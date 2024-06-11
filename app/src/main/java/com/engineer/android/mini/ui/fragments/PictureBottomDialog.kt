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
import com.engineer.android.mini.ui.adapter.AlbumAdapter
import com.engineer.android.mini.ui.adapter.AlbumAdapterAnnotation
import com.engineer.android.mini.ui.adapter.GifAdapter
import com.engineer.android.mini.ui.adapter.GifAdapterAnnotation
import com.engineer.android.mini.ui.viewmodel.CursorQueryViewModel
import com.engineer.common.utils.SystemTools
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.concurrent.thread

/**
 * Created on 2020/10/18.
 * @author rookie
 */
private const val TAG = "PictureBottomDialog"

enum class GalleryType {
    PHOTO, GIF
}

@AndroidEntryPoint
class PictureBottomDialog(val type: GalleryType) : BaseBottomSheetDialog() {

    @Inject
    lateinit var imageList: ArrayList<Uri>

    @AlbumAdapterAnnotation
    @Inject
    lateinit var albumAdapter: AlbumAdapter

    @GifAdapterAnnotation
    @Inject
    lateinit var gifAdapter: GifAdapter

    private lateinit var adapter: RecyclerView.Adapter<*>

    private val cursorQueryViewModel: CursorQueryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = if (type == GalleryType.GIF) gifAdapter else albumAdapter
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val columns = 3

        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
        }
        recyclerView.layoutManager = flexboxLayoutManager
        recyclerView.adapter = adapter

        cursorQueryViewModel.imageResults.observe(this) {
            it.size.toString().toast()
            imageList.clear()
            imageList.addAll(it)
            adapter.notifyDataSetChanged()
            handleUri(it)
        }

        if (type == GalleryType.PHOTO) cursorQueryViewModel.loadImages() else cursorQueryViewModel.loadGifs()

        cursorQueryViewModel.videoResults.observe(this) {
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