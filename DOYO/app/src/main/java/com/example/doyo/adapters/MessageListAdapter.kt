package com.example.doyo.adapters

import android.R.id.message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doyo.R
import com.example.doyo.models.Message
import com.example.doyo.services.AccountService


class MessageListAdapter(
    private val inflater: LayoutInflater, private val messages: MutableList<Message>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_I = R.layout.i_message
    private val VIEW_OTHER = R.layout.other_message

    inner class IMessageHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val text = view.findViewById<TextView>(R.id.i_message)
        private val time = view.findViewById<TextView>(R.id.i_time)

        fun bind(message: Message) {
            text.text = message.value
            time.text = message.time
        }

    }

    inner class OtherMessageHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val text = view.findViewById<TextView>(R.id.other_message)
        private val time = view.findViewById<TextView>(R.id.other_time)
        private val username = view.findViewById<TextView>(R.id.other_username)
        private val icon = view.findViewById<ImageView>(R.id.other_icon)

        fun bind(message: Message) {
            text.text = message.value
            time.text = message.time
            username.text = message.username
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].username == AccountService.username)
            VIEW_I
        else
            VIEW_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflater.inflate(viewType, parent, false)

        return if (viewType == VIEW_I)
            IMessageHolder(view)
        else
            OtherMessageHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val member = messages[position]
        if (holder.itemViewType == VIEW_I)
            (holder as IMessageHolder).bind(member)
        else
            (holder as OtherMessageHolder).bind(member)
    }

    override fun getItemCount(): Int = messages.size
}