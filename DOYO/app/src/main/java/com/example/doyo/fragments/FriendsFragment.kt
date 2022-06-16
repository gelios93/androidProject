package com.example.doyo.fragments

import android.os.Bundle
import android.view.View
import com.example.doyo.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.doyo.adapters.SectionListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doyo.databinding.FragmentFriendsBinding
import com.example.doyo.models.User
import com.example.doyo.services.AccountService
import com.example.doyo.services.HttpService
import com.google.gson.Gson

class FriendsFragment: Fragment(R.layout.fragment_friends) {
    private lateinit var binding: FragmentFriendsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFriendsBinding.inflate(layoutInflater, container, false)
        val requests = if (AccountService.requests.isEmpty()) mutableListOf() else AccountService.requests
        val friends = if (AccountService.friends.isEmpty()) mutableListOf() else AccountService.friends
        val friendSection =  Section("YOUR FRIENDS", friends, "friends", "YOU DON'T HAVE ANY FRIENDS")
        val requestSection =  Section("REQUESTS", requests, "requests", "YOU DON'T HAVE ANY REQUESTS")

        val sections: MutableList<Section> = mutableListOf(friendSection)
        if (requestSection.list.isNotEmpty()) {
            sections.add(requestSection)
            sections.reverse()
        }

        val adapter = SectionListAdapter(inflater, sections, null)

        val actionListener = object: SectionListAdapter.ActionListener {
            override fun deleteFriend(username: String): Boolean {
                val response = context?.let { it -> HttpService.deleteFriend(it, username) }
                println(username)
                println(response?.toString(2))
                return if (response?.has("message") == true) {
                    if (response.get("message") == "success") {
                        AccountService.friends = Gson().fromJson(response.getString("friends"), Array<User>::class.java).toMutableList()
                        Toast.makeText(context, "User $username is deleted from friends", Toast.LENGTH_SHORT).show()
                        true
                    } else {
                        Toast.makeText(context, "Error while deleting friend", Toast.LENGTH_SHORT).show()
                        false
                    }
                } else
                    false
            }

            override fun acceptRequest(username: String): Boolean {
                val response = context?.let { it -> HttpService.acceptRequest(it, username) }
                println(username)
                println(response.toString())
                if (response?.has("message") == true)
                    if (response.get("message") == "success") {
                        AccountService.friends = Gson().fromJson(response.getString("friends"), Array<User>::class.java).toMutableList()
                        AccountService.requests = Gson().fromJson(response.getString("requests"), Array<User>::class.java).toMutableList()
                        return true
                    }
                Toast.makeText(context, "Error while accepting request", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun refuseRequest(username: String): Boolean {
                val response = context?.let { it -> HttpService.cancelRequest(it, username) }
                println(response.toString())
                if (response?.has("message") == true)
                    if (response.get("message") == "success") {
                        AccountService.requests = Gson().fromJson(response.getString("requests"), Array<User>::class.java).toMutableList()
                        return true
                    }
                Toast.makeText(context, "Error while canceling request", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        adapter.listener = actionListener
        binding.rvSections.adapter = adapter
        binding.rvSections.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    companion object {
        fun newInstance() = FriendsFragment()
    }

    data class Section (val title: String, val list: MutableList<User>, val type: String, val hint: String)
}