package com.engineer.android.mini.jetpack.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(TAG, "onAttach() called with: context = $context")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG, "onActivityCreated() called with: savedInstanceState = $savedInstanceState")
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        Log.e(TAG, "onAttachFragment() called with: childFragment = $childFragment")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart() called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            Log.e(TAG, "onCreate(): param1 = $param1,param2 = $param2")
        }
        fooViewModel = ViewModelProvider(this).get(FooViewModel::class.java)
//        fooViewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.AndroidViewModelFactory.getInstance(MinApp.INSTANCE)
//        ).get(FooViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(
            TAG,
            "onCreateView() called with: inflater = $inflater, container = $container, savedInstanceState = $savedInstanceState"
        )
        // Inflate the layout for this fragment
        return inflater.inflate(provideLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(
            TAG,
            "onViewCreated() called with: view = $view, savedInstanceState = $savedInstanceState"
        )
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy() called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG, "onDetach() called")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop() called")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.e(TAG, "onHiddenChanged() called with: hidden = $hidden")
    }


    abstract fun provideLayoutId(): Int
}