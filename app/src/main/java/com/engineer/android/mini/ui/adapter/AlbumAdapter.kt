package com.engineer.android.mini.ui.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.engineer.android.mini.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext


@Module
@InstallIn(FragmentComponent::class)
object AlbumAdapterProvider {

    private lateinit var list: ArrayList<Uri>

    @Provides
    fun provideList(): ArrayList<Uri> {
        list = ArrayList()
        return list
    }

    @Provides
    fun providerAdapter(
        @ActivityContext context: Context,
    ): AlbumAdapter {
        val size = context.resources.displayMetrics.widthPixels / 3
        return AlbumAdapter(context, list, size)
    }
}

class AlbumAdapter(val context: Context, val imageList: ArrayList<Uri>, val imageSize: Int) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.album_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = imageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.layoutParams.width = imageSize
        holder.imageView.layoutParams.height = imageSize
        val uri = imageList[position]
        val options =
            RequestOptions().placeholder(R.drawable.album_loading_bg).override(imageSize, imageSize)
        Glide.with(context).load(uri).apply(options).into(holder.imageView)
    }

}