package com.engineer.mvp.mini.nav.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.engineer.mvp.mini.R
import com.engineer.mvp.mini.databinding.FragmentA1Binding

class A1Fragment : Fragment() {
    private var _binding: FragmentA1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentA1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoToA2.setOnClickListener {
            findNavController().navigate(R.id.action_a1_to_nav_a2_graph)
        }

        binding.btnGoToB.setOnClickListener {
            findNavController().navigate(R.id.action_a1_to_b)
        }

        binding.btnGoToA21.setOnClickListener {
            val bundle = bundleOf("navigateToA21" to true)
            findNavController().navigate(R.id.action_a1_to_nav_a2_graph, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}