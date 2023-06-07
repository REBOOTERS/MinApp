package com.engineer.android.mini.ui.tabs.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.engineer.android.mini.ui.tabs.ui.fragments.ItemFragment
import com.engineer.android.mini.ui.tabs.ui.fragments.PhotoFragment
import com.engineer.android.mini.ui.tabs.ui.fragments.SimpleFragment

class FragmentsAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return FragmentFactory.createFragment(position)
    }
}

object FragmentFactory {
    fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SimpleFragment.newInstance("1", "2")
            1 -> PhotoFragment.newInstance("3", "4")
            else -> ItemFragment.newInstance(3)
        }
    }
}