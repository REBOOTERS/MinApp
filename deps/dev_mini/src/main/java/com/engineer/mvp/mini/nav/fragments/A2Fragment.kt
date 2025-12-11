package com.engineer.mvp.mini.nav.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.engineer.mvp.mini.R
import com.engineer.mvp.mini.databinding.FragmentA2Binding

class A2Fragment : Fragment() {
    private var _binding: FragmentA2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentA2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 检查是否需要直接跳转到 A21
        val navigateToA21 = arguments?.getBoolean("navigateToA21", false) ?: false
        if (navigateToA21) {
            arguments?.remove("navigateToA21")
            // 跳转时将 A2 从返回栈中移除，这样从 A21 返回时会直接回到 A1
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.a2Fragment, true)
                .build()
            findNavController().navigate(R.id.action_a2_to_a21, null, navOptions)
        }

        binding.btnGoToA21.setOnClickListener {
            findNavController().navigate(R.id.action_a2_to_a21)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}