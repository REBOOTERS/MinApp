package com.engineer.android.mini.ui.pure

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.dp

class DuDuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_du_du)

        val img1 = findViewById<ImageView>(R.id.img_1)
        val img2 = findViewById<ImageView>(R.id.img_2)
        val img3 = findViewById<ImageView>(R.id.img_3)
        val tv1 = findViewById<TextView>(R.id.tv1)
        tv1.setOnClickListener {  }


        val options = RequestOptions()

        options.transform(RoundedCorners(5.dp))

        Glide.with(this)
            .load(R.drawable.avatar)
            .placeholder(R.drawable.cat)
            .apply(options)
            .into(img1)

        options.transform(RoundedCorners(10.dp))
        Glide.with(this)
            .load(R.drawable.cute)
            .apply(options)
            .into(img2)

        Glide.with(this)
            .load(R.drawable.lovely)
            .into(img3)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.e("DuDuActivity", "onTouchEvent: ${event?.action}")
        return super.onTouchEvent(event)
    }
}