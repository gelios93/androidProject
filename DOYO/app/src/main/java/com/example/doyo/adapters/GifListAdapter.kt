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
import com.example.doyo.R
import com.example.doyo.saveGifToGallery
import com.squareup.picasso.Picasso
import com.bumptech.glide.load.resource.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class GifListAdapter(private val inflater: LayoutInflater, private var animations: MutableList<String>):
    ListAdapter<GifDrawable, GifListAdapter.ViewHolder>(GifDiffCallback) {

    inner class ViewHolder(view: View, parentContext: Context): RecyclerView.ViewHolder(view){
        private val gifView = view.findViewById<GifImageView>(R.id.gifProfileImage)
        private val btnDownload = view.findViewById<Button>(R.id.btnGifDownloadProfile)
        private val context = parentContext

        fun bind(gif: GifDrawable, name: String){
            Glide.with(context).load(gif).into(gifView)
            Picasso.get().load(R.drawable.gif).into(gifView)

            btnDownload.setOnClickListener {
                Toast.makeText(context, "GIF is saved!", Toast.LENGTH_SHORT).show()
                saveGifToGallery(name, gifView, context)
                btnDownload.isClickable = false
                btnDownload.text = "DOWNLOADED"
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_gif, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gif = getItem(position)
        holder.bind(gif, animations[position])
    }

    object GifDiffCallback: DiffUtil.ItemCallback<GifDrawable>() {
        override fun areItemsTheSame(
            oldItem: GifDrawable,
            newItem: GifDrawable
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: GifDrawable,
            newItem: GifDrawable
        ): Boolean { return oldItem.equals(newItem) }
    }
}