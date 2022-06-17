package com.example.doyo.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.example.doyo.R
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doyo.adapters.GifListAdapter
import com.example.doyo.adapters.SearchListAdapter
import com.example.doyo.databinding.FragmentGalleryBinding
import com.example.doyo.services.SocketService
import java.util.ArrayList

class GalleryFragment: Fragment(R.layout.fragment_gallery) {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var animations: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val array = arguments?.getStringArray("animations")
        print("ARRAY " + array)
        if (array != null) {
            animations = array.toMutableList()
        }
        else
            animations = mutableListOf()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGalleryBinding.inflate(layoutInflater, container, false)
        animations = animations.toMutableList()

        if (animations.isNotEmpty()) {
            binding.hint.isVisible = false
        }

        val adapter = GifListAdapter(layoutInflater)
        binding.gifRecyclerView.adapter = adapter
        binding.gifRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter.submitList(animations)

        return binding.root
    }

    companion object {
        fun newInstance(animations: List<String>): GalleryFragment {
            val args = Bundle()
            args.putStringArray("animations", animations.toTypedArray())
            val fragment = GalleryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}