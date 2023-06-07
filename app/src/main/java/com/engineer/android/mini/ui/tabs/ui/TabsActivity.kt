package com.engineer.android.mini.ui.tabs.ui

import android.os.Bundle
import android.widget.TextView
import com.engineer.android.mini.databinding.ActivityTabsBinding
import com.engineer.android.mini.ui.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class TabsActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityTabsBinding
    private lateinit var adapter: FragmentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTabsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        adapter = FragmentsAdapter(this)
        viewBinding.viewPager2.adapter = adapter
        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewPager2) { tab, position ->
            val text = TextView(this)
            text.text = when (position) {
                0 -> "Simple"
                1 -> "Photo"
                else -> "Item"
            }
            tab.customView = text
        }.attach()

    }
}