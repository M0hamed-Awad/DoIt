package com.example.doit.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.doit.ui.fragments.CompletedTasksFragment
import com.example.doit.ui.fragments.InProgressTasksFragment
import com.example.doit.ui.fragments.OverdueTasksFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> InProgressTasksFragment()
            1 -> CompletedTasksFragment()
            else -> OverdueTasksFragment()
        }
    }
}