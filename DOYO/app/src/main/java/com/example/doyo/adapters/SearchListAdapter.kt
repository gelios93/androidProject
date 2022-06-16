package com.example.doyo.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.doyo.R
import com.example.doyo.SERVER_IP
import com.example.doyo.fragments.SearchFragment
import com.squareup.picasso.Picasso

class SearchListAdapter(private val inflater: LayoutInflater, var listener: OnItemClickListener?):
    ListAdapter<SearchFragment.SearchResult, SearchListAdapter.ViewHolder>(SearchDiffCallback()) {

    interface OnItemClickListener{
        fun onRequestClick(position: Int): Boolean
    }

    inner class ViewHolder(view: View, parentContext: Context): RecyclerView.ViewHolder(view){
        private val icon = view.findViewById<ImageView>(R.id.iconSearch)
        private val username = view.findViewById<TextView>(R.id.usernameSearch)
        private val exp = view.findViewById<TextView>(R.id.expSearch)
        private val button = view.findViewById<ImageView>(R.id.btnRequest)

        private val context = parentContext
        private val clickAnim = AnimationUtils.loadAnimation(parentContext, R.anim.anim_draw_item)

        fun bind(item: SearchFragment.SearchResult){
            button.isEnabled = true
            button.isVisible = true
            button.background.setTint(context.resources.getColor(R.color.gray_palette, null))
            if (item.user.icon == " ") {
                val drawable = VectorDrawableCompat.create(context.resources, R.drawable.basic_icon, context.theme)
                icon.setImageBitmap(drawable?.toBitmap()!!)
            }
            else
                Picasso.get().load("$SERVER_IP/doyo/images/icons/${item.user.icon}").into(icon)

            username.text = item.user.username
            exp.text = "exp ${item.user.experience}"

            if (item.isFriend) {
                button.isEnabled = false
                button.isVisible = false
            }
            else
                button.setOnClickListener {
                    it.startAnimation(clickAnim)
                    if (listener?.onRequestClick(currentList.indexOf(item)) == true) {
                        button.isEnabled = false
                        button.background.setTint(context.resources.getColor(R.color.light_gray, null))
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.search_item, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user= getItem(position)
        holder.bind(user)
    }

    class SearchDiffCallback: DiffUtil.ItemCallback<SearchFragment.SearchResult>(){
        override fun areItemsTheSame(
            oldItem: SearchFragment.SearchResult,
            newItem: SearchFragment.SearchResult
        ): Boolean = oldItem  == newItem

        override fun areContentsTheSame(
            oldItem: SearchFragment.SearchResult,
            newItem: SearchFragment.SearchResult
        ): Boolean {
            return oldItem.user.username == newItem.user.username
                    && oldItem.user.experience == newItem.user.experience
                    && oldItem.user.icon == newItem.user.icon
                    && oldItem.isFriend == newItem.isFriend
        }
    }
}