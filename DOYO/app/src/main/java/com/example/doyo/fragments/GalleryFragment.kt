package com.example.doyo.fragments

import android.os.Bundle
import android.view.View
import com.example.doyo.R
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.example.doyo.databinding.FragmentGalleryBinding

class GalleryFragment: Fragment(R.layout.fragment_gallery) {
    private lateinit var binding: FragmentGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = GalleryFragment()
    }
}