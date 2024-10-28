package com.example.quickcart.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quickcart.adapter.OrderPagerAdapter
import com.example.quickcart.databinding.FragmentOrderBinding
import com.google.android.material.tabs.TabLayoutMediator

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using ViewBinding
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the ViewPager2 adapter
        val adapter = OrderPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Link TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Active"
                1 -> "Completed"
                2 -> "Cancelled"
                else -> "Tab ${position + 1}"
            }
        }.attach()

        // Handle back button click
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
