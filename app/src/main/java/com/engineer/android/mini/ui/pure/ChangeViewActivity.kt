package com.engineer.android.mini.ui.pure

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.LayoutInflaterCompat
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityChangeViewBinding
import com.engineer.android.mini.ext.toast

class ChangeViewActivity : AppCompatActivity() {
    private val TAG = "ChangeViewActivity"
    private lateinit var viewBinding: ActivityChangeViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val inflater = LayoutInflater.from(this)
        inflater.factory2 = object : LayoutInflater.Factory2 {
            override fun onCreateView(
                parent: View?,
                name: String,
                context: Context,
                attrs: AttributeSet
            ): View? {
                Log.i(TAG, "name = $name")
                val count = attrs.attributeCount
                for (i in 0 until count) {
                    Log.i(TAG, "name = " + attrs.getAttributeName(i) + ",value= " + attrs.getAttributeValue(i));
                }
                Log.i(TAG,"==============================================")
                if (name == "TextView") {
                    val view = MyTextView(context, attrs, 0)
                    return view
                }
                return null
            }

            override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
                return null
            }

        }

        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        viewBinding = ActivityChangeViewBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)



        viewBinding.dynamicLayout.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                child?.let {
                    Log.i(TAG, "add a1 " + it)
                    if (it is TextView) {
                        Log.i(TAG, "add a " + it)

                    }
                }
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {

            }

        })
        val dynamicText = TextView(this)
        dynamicText.text = "Android & Android"
        viewBinding.dynamicLayout.addView(dynamicText)

        viewBinding.updateText.setOnClickListener {
            val origin = "中文_Android_${System.currentTimeMillis()}"
            viewBinding.originalText.text = origin
            origin.toast()
            viewBinding.keepText.text = "iOS_${System.currentTimeMillis()}"
            dynamicText.text = "Android & Android"
        }
    }

    private inner class MyTextView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : AppCompatTextView(context, attrs, defStyleAttr) {

        val regex = Regex("Android")

        override fun onDraw(canvas: Canvas) {
            if (text.contains("Android")) {
                text = text.replaceFirst(regex, "android")
            }
            super.onDraw(canvas)

        }
    }
}