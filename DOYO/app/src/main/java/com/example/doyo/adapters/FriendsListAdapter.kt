package com.example.doyo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.doyo.R
import com.example.doyo.SERVER_IP
import com.example.doyo.models.User
import com.squareup.picasso.Picasso

class FriendsListAdapter(private val inflater: LayoutInflater,
                         private val users: MutableList<User>,
                         var listener: OnItemClickListener?,
                         val type: String):
    RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun onDeleteClick(position: Int)
        fun onAcceptClick(position: Int)
        fun onRefuseClick(position: Int)
    }

    inner class ViewHolder(view: View, parentContext: Context): RecyclerView.ViewHolder(view){
        private val exp = view.findViewById<TextView>(R.id.exp)
        private val icon = view.findViewById<ImageView>(R.id.iconFriend)
        private val username = view.findViewById<TextView>(R.id.usernameFriend)
        private val btnDelete = view.findViewById<ImageButton>(R.id.btnDeleteFriend)
        private val btnAccept = view.findViewById<AppCompatImageView>(R.id.btnAccept)
        private val btnRefuse = view.findViewById<AppCompatImageView>(R.id.btnRefuse)

        private val context = parentContext
        private val clickAnim = AnimationUtils.loadAnimation(parentContext, R.anim.anim_draw_item)

        fun bind(user: User){
            if (user.icon == " ") {
                val drawable = VectorDrawableCompat.create(context.resources, R.drawable.basic_icon, context.theme)
                icon.setImageBitmap(drawable?.toBitmap()!!)
            }
            else
                Picasso.get().load("$SERVER_IP/doyo/images/icons/${user.icon}").into(icon)

            username.text = user.username
            exp.text = "exp ${user.experience}"

            btnDelete?.setOnClickListener {
                it.startAnimation(clickAnim)
                listener?.onDeleteClick(users.indexOf(user))
            }
            btnAccept?.setOnClickListener {
                it.startAnimation(clickAnim)
                listener?.onAcceptClick(users.indexOf(user))
            }
            btnRefuse?.setOnClickListener {
                it.startAnimation(clickAnim)
                listener?.onRefuseClick(users.indexOf(user))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (type == "requests")
            inflater.inflate(R.layout.item_request, parent, false)
        else
            inflater.inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }
}