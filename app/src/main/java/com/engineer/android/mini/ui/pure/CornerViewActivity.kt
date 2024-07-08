package com.engineer.android.mini.ui.pure

import android.graphics.BitmapFactory
import android.graphics.Outline
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityCornerViewBinding
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.util.ImageUtils

class CornerViewActivity : BaseActivity() {


    private lateinit var realBinding: ActivityCornerViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realBinding = ActivityCornerViewBinding.inflate(layoutInflater)
        setContentView(realBinding.root)


        realBinding.phone.clipToOutline = true
        realBinding.slider.addOnChangeListener { _, value, fromUser ->
            Log.e(TAG, "onCreate() called with: value = $value, fromUser = $fromUser")
            realBinding.phone.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    val radius = value * 25.dp.toFloat()
                    outline?.setRoundRect(0, 0, view!!.width, view.height, radius)
                }
            }
        }
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.wallpaper_landscape)
        val cornerBitmap = ImageUtils.toRoundCorner(
            bitmap, 20.dp, ImageUtils.CORNER_BOTTOM_LEFT or ImageUtils.CORNER_BOTTOM_RIGHT or ImageUtils.CORNER_TOP_LEFT
        )
        realBinding.phone2.setImageBitmap(cornerBitmap)
    }
}