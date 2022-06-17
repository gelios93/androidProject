package com.example.doyo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.doyo.R
import com.example.doyo.SERVER_IP
import pl.droidsonroids.gif.GifImageView

class GifListAdapter(private val inflater: LayoutInflater):
    ListAdapter<String, GifListAdapter.ViewHolder>(GifDiffCallback()) {

    inner class ViewHolder(view: View, parentContext: Context): RecyclerView.ViewHolder(view){
        private val gifView = view.findViewById<GifImageView>(R.id.gifProfileImage)
        private val btnDownload = view.findViewById<Button>(R.id.btnGifDownloadProfile)
        private val context = parentContext

        fun bind(gif: String){
            Glide.with(context)
                .load("$SERVER_IP/doyo/images/animations/$gif")
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(gifView)
            btnDownload.setOnClickListener {
                Toast.makeText(context, "GIF is saved!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_gif, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gif = getItem(position)
        holder.bind(gif)
    }

    class GifDiffCallback: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean { return oldItem == newItem }
    }
}