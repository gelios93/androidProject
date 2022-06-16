package com.example.doyo.adapters

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.doyo.*
import com.example.doyo.adapters.InviteFriendsListAdapter.ViewHolder
import com.example.doyo.databinding.ItemFriendInviteBinding
import com.example.doyo.models.User
import com.example.doyo.services.AccountService
import com.example.doyo.services.SocketService
import com.squareup.picasso.Picasso

class InviteFriendsListAdapter
    (private val inflater: LayoutInflater, private val dialog: Dialog?) :
    ListAdapter<User, InviteFriendsListAdapter.ViewHolder>(UserDiffCallback()) {

    inner class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {


        private val layout = view.findViewById<ConstraintLayout>(R.id.invite_friends_item)
        private val icon = view.findViewById<ImageView>(R.id.invite_friends_avatar)
        private val username = view.findViewById<TextView>(R.id.invite_friends_username)


        fun bind(user: User) {
            Picasso.get().load("$SERVER_IP/doyo/images/icons/${user.icon}").into(icon)
            username.text = user.username
            layout.isClickable = true
            layout.setOnClickListener {
                println("invite ${user.username}")
                SocketService.socket.emit("friendInvite", user.username)
                dialog?.dismiss()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_friend_invite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean  = oldItem == newItem


        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {
            return oldItem.icon == newItem.icon &&
                    oldItem.username == newItem.username
        }
    }

}