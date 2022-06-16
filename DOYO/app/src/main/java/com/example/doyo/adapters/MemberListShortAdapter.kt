package com.example.doyo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.doyo.R
import com.example.doyo.SERVER_IP
import com.example.doyo.models.User
import com.example.doyo.services.AccountService
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

class MemberListShortAdapter (
    private val inflater: LayoutInflater) :
    ListAdapter<User, MemberListShortAdapter.ViewHolder>(FriendDiffCallback()) {

    inner class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {


        private val icon = view.findViewById<ImageView>(R.id.member_short_avatar)
        private val card = view.findViewById<MaterialCardView>(R.id.member_short_card)

        fun bind(member: User) {
            if (member.icon == " ")
                icon.setImageBitmap(AccountService.icon)
            else
                Picasso.get().load("$SERVER_IP/doyo/images/icons/${member.icon}").into(icon)
            card.strokeColor = member.color
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.member_item_short, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = getItem(position)
        holder.bind(member)
    }

    class FriendDiffCallback : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean  = oldItem == newItem


        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {
            return oldItem.username == newItem.username && oldItem.icon == newItem.icon && oldItem.experience == newItem.experience
        }
    }


}