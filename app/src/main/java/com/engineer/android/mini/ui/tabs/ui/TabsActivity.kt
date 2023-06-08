package com.engineer.android.mini.ui.tabs.ui

import android.os.Bundle
import android.util.Log
import com.engineer.android.mini.databinding.ActivityTabsBinding
import com.engineer.android.mini.ui.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class TabsActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityTabsBinding
    private lateinit var adapter: FragmentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTabsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        showStatusBar(false)
        adapter = FragmentsAdapter(this)
        viewBinding.viewPager2.adapter = adapter
        TabLayoutMediator(viewBinding.tabLayout, viewBinding.viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Simple"
                1 -> "Photo"
                else -> "Item"
            }

        }.attach()
        viewBinding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabSelected() called with: tab = ${tab?.text}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabUnselected() called with: tab = $tab")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d(TAG, "onTabReselected() called with: tab = $tab")
            }
        })
    }
}