package com.example.doyo.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.doyo.R
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.doyo.SERVER_IP
import com.example.doyo.activities.ProfileActivity
import com.example.doyo.adapters.GifListAdapter
import com.example.doyo.adapters.SearchListAdapter
import com.example.doyo.databinding.FragmentGalleryBinding
import com.example.doyo.services.AccountService
import com.example.doyo.services.SocketService
import java.util.ArrayList

class GalleryFragment: Fragment(R.layout.fragment_gallery) {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var animations: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val array = arguments?.getStringArray("animations")
        println("ARRAY " + array)
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

//        println("${animations.size} SIIIIIIIIIIIIIIIIIIIIIIIIIIIIZE")
//
//        if (animations.size == 0) {
//            println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
//            binding.hint.visibility = View.VISIBLE
//        }
//        else {
//            println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH")
//            binding.hint.visibility = View.INVISIBLE
//        }

        val animationDrawable = mutableListOf<GifDrawable>()

        val adapter = GifListAdapter(layoutInflater, animations)
        binding.gifRecyclerView.adapter = adapter
        binding.gifRecyclerView.layoutManager = LinearLayoutManager(context)
        val run = Runnable {
            for (name in animations){
                val drawable = Glide.with(context)
                    .asGif()
                    .load("$SERVER_IP/doyo/images/animations/$name")
                    .submit().get()
                animationDrawable.add(drawable)
                Handler(Looper.getMainLooper()).post {
                    adapter.submitList(animationDrawable.toList())
                }
            }
        }
        Thread(run).start()

//        adapter.submitList(animationBitmaps)



        return binding.root
    }

    companion object {
        fun newInstance(animations: List<String>): GalleryFragment {
            println("NEW INSTANCE")
            val args = Bundle()
            args.putStringArray("animations", animations.toTypedArray())
            val fragment = GalleryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}