package com.example.quickcart.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.quickcart.fragment.ActiveOrdersFragment
import com.example.quickcart.fragment.CancelledOrdersFragment
import com.example.quickcart.fragment.CompleteOrdersFragment

class OrderPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ActiveOrdersFragment()
            1 -> CompleteOrdersFragment()
            2 -> CancelledOrdersFragment()
            else -> Fragment()
        }
    }
}