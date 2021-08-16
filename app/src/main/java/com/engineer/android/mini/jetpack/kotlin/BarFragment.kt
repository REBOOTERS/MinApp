package com.engineer.android.mini.jetpack.kotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.jetpack.base.ARG_PARAM1
import com.engineer.android.mini.jetpack.base.ARG_PARAM2
import com.engineer.android.mini.jetpack.base.SimpleBaseFragment


class BarFragment : SimpleBaseFragment() {

    override fun provideLayoutId(): Int {
        return R.layout.fragment_bar
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = view.findViewById<TextView>(R.id.text)
        fooViewModel.foo.observe(viewLifecycleOwner, {
            text.text = "result is $it"
            Log.e(TAG, "$it")
        })

        fooViewModel.fooMap.observe(viewLifecycleOwner, {
            it.toast()
        })

        fooViewModel.doFoo()
        fooViewModel.doFoo2()

        testSetAndPost()
    }

    private fun testSetAndPost() {
        fooViewModel.mainValue.observe(viewLifecycleOwner, {
            Log.e("testSetAndPost", "mainValue =$it")
        })
        fooViewModel.threadValue.observe(viewLifecycleOwner, {
            Log.e("testSetAndPost", "threadValue =$it")
        })
        fooViewModel.doUpdate()
    }
}