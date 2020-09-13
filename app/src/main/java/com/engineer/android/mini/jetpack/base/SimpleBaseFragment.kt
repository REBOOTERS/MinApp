package com.engineer.android.mini.jetpack.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.engineer.android.mini.MinApp
import com.engineer.android.mini.jetpack.viewmodel.FooViewModel

/**
 * Created on 2020/9/13.
 * @author rookie
 */

internal const val ARG_PARAM1 = "param1"
internal const val ARG_PARAM2 = "param2"


abstract class SimpleBaseFragment : Fragment() {
    val TAG = "SimpleBaseFragment"
    private var param1: String? = null
    private var param2: String? = null

    lateinit var fooViewModel: FooViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            Log.e(TAG, "onCreate: param1 = $param1")
            Log.e(TAG, "onCreate: param2 = $param2")
        }
        fooViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(MinApp.INSTANCE)
        ).get(FooViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(provideLayoutId(), container, false)
    }

    abstract fun provideLayoutId(): Int
}