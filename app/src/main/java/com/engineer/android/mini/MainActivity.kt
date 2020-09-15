package com.engineer.android.mini

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.proguards.A
import com.engineer.android.mini.proguards.B
import com.engineer.android.mini.proguards.BlankFragment
import com.engineer.android.mini.proguards.Utils
import com.engineer.android.mini.ui.JetpackActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jetpack.setOnClickListener { gotoPage(JetpackActivity::class.java) }
    }

    private fun gotoPage(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }

    override fun onResume() {
        super.onResume()
        proguardTest()
    }

    private fun proguardTest() {
        val fragment = BlankFragment()
        fragment.arguments?.putString("value", "proguard")
        Utils.test1()
        Utils.test3(this)
        Utils.MyBuilder().setName("this")

        val a = A()
        a.test2()
        val b = B()
        println(b.hashCode())
    }
}
