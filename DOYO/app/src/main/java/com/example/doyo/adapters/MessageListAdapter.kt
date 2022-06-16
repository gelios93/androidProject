package com.example.doyo.adapters

import android.R.id.message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.doyo.R
import com.example.doyo.models.Message
import com.example.doyo.services.AccountService
import com.google.android.material.card.MaterialCardView


class MessageListAdapter(
    private val inflater: LayoutInflater, private val messages: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_I = R.layout.i_message
    private val VIEW_OTHER = R.layout.other_message

    inner class IMessageHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val text = view.findViewById<TextView>(R.id.i_message)
        private val time = view.findViewById<TextView>(R.id.i_time)
        private val card = view.findViewById<MaterialCardView>(R.id.i_card)

        fun bind(message: Message) {
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

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].user.username == AccountService.username)
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
        else {
            if (messages.size > 1 && member.user.username == messages[position - 1].user.username)
                (holder as OtherMessageHolder).bind(member, true)
            else
                (holder as OtherMessageHolder).bind(member, false)
        }
    }

    override fun getItemCount(): Int = messages.size
}