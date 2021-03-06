package com.example.doyo.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.doyo.R
import com.example.doyo.SERVER_IP
import com.example.doyo.models.Message
import com.example.doyo.saveGifToGallery
import com.example.doyo.services.AccountService
import com.example.doyo.services.SocketService
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import pl.droidsonroids.gif.GifImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer


class MessageListAdapter(
    private val inflater: LayoutInflater,
    private val messages: MutableList<Message>,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_I = R.layout.item_message_i
    private val VIEW_OTHER = R.layout.item_message_other
    private val VIEW_GIF = R.layout.item_message_gif

    inner class IMessageHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val text = view.findViewById<TextView>(R.id.i_message)
        private val time = view.findViewById<TextView>(R.id.i_time)
        private val card = view.findViewById<MaterialCardView>(R.id.i_card)

        fun bind(message: Message) {
            message.user!!

            text.text = message.value
            text.setTextColor(message.user.color)
            time.text = message.time
            card.strokeColor = message.user.color
        }

    }

    inner class OtherMessageHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val text = view.findViewById<TextView>(R.id.other_message)
        private val time = view.findViewById<TextView>(R.id.other_time)
        private val username = view.findViewById<TextView>(R.id.other_username)
        private val card = view.findViewById<MaterialCardView>(R.id.other_card)

        fun bind(message: Message, samePerson: Boolean) {
            message.user!!

            text.text = message.value
            text.setTextColor(message.user.color)
            time.text = message.time

            username.text = message.user.username
            username.setTextColor(message.user.color)
            if (samePerson) {
                println("GONE")
                username.visibility = View.GONE
            }
            card.strokeColor = message.user.color
        }

    }

    inner class GifMessageHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val gif = view.findViewById<GifImageView>(R.id.gif_image)
        private val addButton = view.findViewById<Button>(R.id.gif_addButton)
        private val downloadButton = view.findViewById<Button>(R.id.gif_downloadButton)

        fun bind(message: Message) {
            Glide.with(context)
                .load("$SERVER_IP/doyo/images/animations/${message.value}")
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(gif)

            addButton.setOnClickListener {
                SocketService.socket.emit("addGif", message.value)
                Toast.makeText(context, "Gif has been added to your profile", Toast.LENGTH_SHORT).show()
                addButton.isClickable = false
                addButton.text = "ADDED"
                addButton.setBackgroundResource(R.drawable.button_clicked)
                AccountService.animations.add(message.value)
            }

            downloadButton.setOnClickListener {
                saveGifToGallery(message.value, gif, context)
                Toast.makeText(context, "Gif has been saved in your storage", Toast.LENGTH_SHORT).show()
                downloadButton.isClickable = false
                downloadButton.text = "DOWNLOADED"
                downloadButton.setBackgroundResource(R.drawable.button_clicked)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].user == null -> VIEW_GIF
            messages[position].user!!.username == AccountService.username -> VIEW_I
            else -> VIEW_OTHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflater.inflate(viewType, parent, false)

        return when (viewType) {
            VIEW_GIF -> GifMessageHolder(view)
            VIEW_I -> IMessageHolder(view)
            else -> OtherMessageHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val member = messages[position]

        if (holder.itemViewType == VIEW_GIF)
            (holder as GifMessageHolder).bind(member)
        else if (holder.itemViewType == VIEW_I)
            (holder as IMessageHolder).bind(member)
        else {
            if (messages.size > 1 && member.user?.username == messages[position - 1].user?.username)
                (holder as OtherMessageHolder).bind(member, true)
            else
                (holder as OtherMessageHolder).bind(member, false)
        }
    }

    override fun getItemCount(): Int = messages.size


}