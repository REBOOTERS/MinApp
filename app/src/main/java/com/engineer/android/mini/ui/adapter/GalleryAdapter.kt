package com.engineer.android.mini.ui.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.pure.GifPlayerActivity
import com.engineer.android.mini.util.ImageUtils
import com.engineer.common.utils.AndroidFileUtils
import com.engineer.common.utils.SystemTools
import com.engineer.common.utils.PictureInfoUtil
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import pl.droidsonroids.gif.GifImageView
import java.io.File
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AlbumAdapterAnnotation

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GifAdapterAnnotation


@Module
@InstallIn(FragmentComponent::class)
object AlbumAdapterProvider {

    private lateinit var list: ArrayList<Uri>

    @Provides
    fun provideList(): ArrayList<Uri> {
        list = ArrayList()
        return list
    }

    @AlbumAdapterAnnotation
    @Provides
    fun providerAdapter(
        @ActivityContext context: Context,
    ): AlbumAdapter {
        val size = context.resources.displayMetrics.widthPixels / 3
        return AlbumAdapter(context, list, size)
    }

    @GifAdapterAnnotation
    @Provides
    fun providerGifAdapter(
        @ActivityContext context: Context,
    ): GifAdapter {
        val size = context.resources.displayMetrics.widthPixels / 3
        return GifAdapter(context, list, size)
    }
}

class AlbumAdapter(val context: Context, private val imageList: ArrayList<Uri>, val imageSize: Int) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val lp = imageView.layoutParams
        val tv: TextView = view.findViewById(R.id.info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.album_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = imageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.layoutParams.width = imageSize
        holder.imageView.layoutParams.height = imageSize


        val lp = holder.itemView.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams) {
            lp.flexGrow = 1f
        }

        val uri = imageList[position]
        val options = RequestOptions().placeholder(R.drawable.album_loading_bg).override(imageSize, imageSize)
//        val options = RequestOptions().placeholder(R.drawable.album_loading_bg)

        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inJustDecodeBounds = true
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions)
        val w = bitmapOptions.outWidth
        val h = bitmapOptions.outHeight
        Log.e("AlbumAdapter", "w=$w,h=$h,uri=$uri")
        holder.tv.text = "w=$w,h=$h"
        Glide.with(context).load(uri).apply(options).into(holder.imageView)

        holder.itemView.setOnClickListener {
            val path = SystemTools.getVideoFilePathFromUri(it.context, uri)
            path.toString().toast()
            path?.let { p ->
                if (p.endsWith(".gif") || p.endsWith(".webp")) {
                    Log.i("ImageUtils", "ignore $p")
                    return@setOnClickListener
                }
                GifPlayerActivity.launch(it.context, path)
//                PictureInfoUtil.printExifInfo(p)
//                Log.i("ImageUtils", "start handle $p")
//                val result = ImageUtils.compressImageToSize(p, 400)
//                val targetPath = context.cacheDir.absolutePath + File.separator + "400_kb.jpg"
//                if (ImageUtils.saveByteArrayToFile(result, targetPath)) {
//                    "save success".toast()
//                    Log.i("ImageUtils", "save success")
//                } else {
//                    "save fail".toast()
//                    Log.i("ImageUtils", "save fail")
//                }
            }
        }
    }

}

class GifAdapter(val context: Context, private val imageList: ArrayList<Uri>, val imageSize: Int) :
    RecyclerView.Adapter<GifAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: GifImageView = view.findViewById(R.id.imageView)
        val lp = imageView.layoutParams


        val tv: TextView = view.findViewById(R.id.info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.gif_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = imageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.imageView.layoutParams.width = imageSize
//        holder.imageView.layoutParams.height = imageSize

        if (holder.lp is FlexboxLayoutManager.LayoutParams) {
            holder.lp.flexGrow = 1f
        }
        val uri = imageList[position]
        val path = AndroidFileUtils.getFilePathByUri(context, uri)
        Log.e("GifAdapter", "uri is $uri ,path = $path")
        holder.imageView.setImageURI(uri)

        holder.itemView.setOnClickListener {
            path.toString().toast()
            path?.let {
                PictureInfoUtil.printExifInfo(path)
            }
            GifPlayerActivity.launch(it.context, uri.toString())
        }
    }

}