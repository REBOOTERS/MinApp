package com.engineer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.ext.toast

/**
 * Created on 2022/8/1.
 * @author rookie
 */
open class BasePlaceHolderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "please use global flavor ".toast()
        finish()
    }
}