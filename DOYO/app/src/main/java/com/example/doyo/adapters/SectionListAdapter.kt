package com.example.doyo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doyo.R
import com.example.doyo.fragments.FriendsFragment
import com.example.doyo.models.User

class SectionListAdapter(private val inflater: LayoutInflater,
                         private val sections: MutableList<FriendsFragment.Section>,
                         var listener: ActionListener?,):
    RecyclerView.Adapter<SectionListAdapter.ViewHolder>() {

    interface ActionListener{
        fun deleteFriend(username: String): Boolean
        fun acceptRequest(username: String): Boolean
        fun refuseRequest(username: String): Boolean
    }
    inner class ViewHolder(view: View, parentContext: Context): RecyclerView.ViewHolder(view){
        private val recyclerView = view.findViewById<RecyclerView>(R.id.rvItems)
        private val title = view.findViewById<TextView>(R.id.sectionTitle)
        private val hint = view.findViewById<TextView>(R.id.hintFriends)
        private val context = parentContext

        private fun showHint(list: MutableList<User>, section: FriendsFragment.Section) {
            if (list.isEmpty()) {
                hint.isVisible = true
                hint.text = section.hint
            }
            else hint.isVisible = false
        }

        fun bind(section: FriendsFragment.Section){
            title.text = section.title
            showHint(section.list, section)

            val adapter = FriendsListAdapter(inflater, section.list,null ,section.type)
            val onClickListener = object: FriendsListAdapter.OnItemClickListener {
                override fun onDeleteClick(position: Int) {
                    if (listener?.deleteFriend(section.list[position].username) == true) {
                        section.list.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        showHint(section.list, section)
                    }
                }
                override fun onAcceptClick(position: Int) {
                    if (listener?.acceptRequest(section.list[position].username) == true) {
                        val friend = section.list.removeAt(position)
                        sections[1].list.add(friend)
                        notifyItemChanged(1)
                        if (section.list.isEmpty()) {
                            sections.removeAt(0)
                            notifyItemRemoved(0)
                        }
                        else adapter.notifyItemRemoved(position)

                    }
                }
                override fun onRefuseClick(position: Int) {
                    if (listener?.refuseRequest(section.list[position].username) == true) {
                        section.list.removeAt(position)
                        if (section.list.isEmpty()) {
                            sections.removeAt(0)
                            notifyItemRemoved(0)
                        }
                        else adapter.notifyItemRemoved(position)
                    }
                }
            }
            adapter.listener = onClickListener
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_section, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sections[position])
    }

    override fun getItemCount(): Int {
        return sections.size
    }
}