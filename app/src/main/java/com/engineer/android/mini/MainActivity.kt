package com.engineer.android.mini

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.engineer.android.mini.proguards.A
import com.engineer.android.mini.proguards.B
import com.engineer.android.mini.proguards.Utils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Utils.test1()
        Utils.MyBuilder().setName("this")

        val a = A()
        a.test2()
        val b = B()
        b.test()
    }
}
