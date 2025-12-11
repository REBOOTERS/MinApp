package com.engineer.mvp.mini.nav.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.engineer.mvp.mini.R
import com.engineer.mvp.mini.databinding.FragmentBBinding

class BFragment : Fragment() {
    private var _binding: FragmentBBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoToA.setOnClickListener {
            findNavController().navigate(R.id.action_b_to_a)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}