package com.example.doyo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.doyo.*
import com.example.doyo.fragments.ConfirmationDialog
import com.example.doyo.models.User
import com.example.doyo.services.AccountService
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso

class MemberListLongAdapter (
    private val inflater: LayoutInflater,var supportFragmentManager: FragmentManager):
    ListAdapter<User, MemberListLongAdapter.ViewHolder>(FriendDiffCallback()){

    interface OnItemClickListener{
        fun onDeleteClick(position: Int)
    }

    inner class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {


        private val icon = view.findViewById<ImageView>(R.id.member_long_avatar)
        private val card = view.findViewById<MaterialCardView>(R.id.member_long_card)
        private val username = view.findViewById<TextView>(R.id.member_long_name)
        private val deleteButton = view.findViewById<ImageButton>(R.id.member_long_deleteButton)
        private val addButton = view.findViewById<ImageButton>(R.id.member_long_addButton)
        private val crown = view.findViewById<ImageView>(R.id.member_long_crown)
        private val you = view.findViewById<TextView>(R.id.member_long_you)
        private val friend = view.findViewById<ImageView>(R.id.member_long_friend)


        fun bind(user: User, position: Int) {

            if (user.icon == " ") {
                icon.setImageBitmap(AccountService.icon)
                username.text = AccountService.username
            }
            else {
                Picasso.get().load("$SERVER_IP/doyo/images/icons/${user.icon}").into(icon)
                username.text = user.username
            }

            username.setTextColor(user.color)
            card.strokeColor = user.color

            deleteButton.setOnClickListener {
//                listener?.onDeleteClick(currentList.indexOf(user))
                val dialog = ConfirmationDialog(DELETE_PLAYER, user.username)
                dialog.show(supportFragmentManager, "deletePlayerDialog")
            }

            addButton.setOnClickListener {
                val dialog = ConfirmationDialog(FRIEND_REQUEST, user.username)
                dialog.show(supportFragmentManager, "friendRequestDialog")
            }



            if (user.username != AccountService.username) {
                you.visibility = View.GONE
                if (AccountService.friends.find { friend -> friend.username == user.username } == null)
                    friend.visibility = View.GONE
                else {
                    addButton.setImageResource(R.drawable.already_friend)
                    addButton.isClickable = false
                }
            }
            else {
                addButton.visibility = View.GONE
                friend.visibility = View.GONE
            }

            if (isHost) {
                if (position == 0){
                    deleteButton.visibility = View.GONE
                    addButton.visibility = View.GONE
                }
                else {
                    crown.visibility = View.GONE
                }
            }
            else {
                deleteButton.visibility = View.GONE
                if (position == 0){

                }
                else {
                    crown.visibility = View.GONE
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberListLongAdapter.ViewHolder {
        val view = inflater.inflate(R.layout.item_member_long, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberListLongAdapter.ViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user, position)

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
            return oldItem.icon == newItem.icon &&
                    oldItem.username == newItem.username &&
                    oldItem.experience == newItem.experience &&
                    oldItem.color == newItem.color
        }
    }

}
