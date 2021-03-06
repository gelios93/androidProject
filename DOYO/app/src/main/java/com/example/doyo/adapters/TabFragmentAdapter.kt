package com.example.doyo.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabFragmentAdapter(activity: AppCompatActivity, private val fragments: List<Fragment>): FragmentStateAdapter(activity) {
    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
}