package com.engineer.android.mini.ui.pure

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.gone
import com.engineer.android.mini.ext.show
import pl.droidsonroids.gif.GifImageView

class GifPlayerActivity : AppCompatActivity() {

    companion object {
        const val URI_PATH = "uri_path"

        fun launch(context: Context, path: String) {
            val intent = Intent(context, GifPlayerActivity::class.java)
            intent.putExtra(URI_PATH, path)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif_player)

        val gifImageView: GifImageView = findViewById(R.id.gif_imageView)
        val iv: ImageView = findViewById(R.id.iv)
        val path = intent.getStringExtra(URI_PATH)
        if (path?.endsWith(".gif") == true) {
            val uri = Uri.parse(path)
            gifImageView.setImageURI(uri)
            gifImageView.show()
        } else {
            iv.show()
            Glide.with(this).load(path).into(iv)
        }

    }
}