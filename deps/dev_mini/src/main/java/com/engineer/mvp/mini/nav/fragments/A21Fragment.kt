package com.engineer.mvp.mini.nav.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.engineer.mvp.mini.databinding.FragmentA21Binding

class A21Fragment : Fragment() {
    private var _binding: FragmentA21Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentA21Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToA2.setOnClickListener {
            handleBackNavigation()
        }
    }

    private fun handleBackNavigation() {
        val shouldSkipA2 = arguments?.getBoolean("should_skip_a2", false) ?: false
        if (shouldSkipA2) {
            // 直接返回Activity
            requireActivity().finish()
        } else {
            // 正常返回A2Fragment
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        // 设置自定义返回处理
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}