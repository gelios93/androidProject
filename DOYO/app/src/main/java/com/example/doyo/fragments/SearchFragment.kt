package com.example.doyo.fragments

import android.os.Bundle
import android.view.View
import com.example.doyo.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doyo.adapters.SearchListAdapter
import com.example.doyo.databinding.FragmentSearchBinding
import com.example.doyo.models.User
import com.example.doyo.services.AccountService
import com.example.doyo.services.HttpService
import com.example.doyo.services.SocketService
import com.google.gson.Gson

class SearchFragment: Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private var listResult: MutableList<SearchResult> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        val clickAnim = AnimationUtils.loadAnimation(context, R.anim.anim_draw_item)

        val adapter = SearchListAdapter(layoutInflater, null)
        adapter.listener = object: SearchListAdapter.OnItemClickListener {
            override fun onRequestClick(position: Int): Boolean {
                SocketService.socket.emit("friendRequest", listResult[position].user.username)
                return true
//                val response = context?.let { it -> HttpService.sendRequest(it, listResult[position].user.username) }
//                println(listResult[position].user.username)
//                println(response.toString())
//                if (response?.has("code") == true)
//                    if (response.get("code") == 400) {
//                        Toast.makeText(context, response.get("message").toString(), Toast.LENGTH_SHORT).show()
//                        return true
//                    }
//                if (response?.has("message") == true)
//                    if (response.get("message") == "success") {
//                        Toast.makeText(context, "Friend request is sent!", Toast.LENGTH_SHORT).show()
//                        return true
//                    }
//                Toast.makeText(context, "Error while sending request", Toast.LENGTH_SHORT).show()
//                return false
            }
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        adapter.submitList(listResult)

        binding.btnClear.setOnClickListener{
            it.startAnimation(clickAnim)
            binding.username.text?.clear()
            listResult = mutableListOf()
            binding.hint.isVisible = true
            adapter.submitList(null)
        }

        binding.btnSearch.setOnClickListener {
            it.startAnimation(clickAnim)
            if (binding.username.text.isNotEmpty()) {
                val response = context?.let { it1 -> HttpService.searchUsers(it1, binding.username.text.toString()) }
                println(response?.toString(2))
                val users = Gson().fromJson(response.toString(), Array<User>::class.java).toMutableList()
                listResult = mutableListOf()
                for (user in users) {
                    if (AccountService.friends.contains(user))
                        listResult.add(SearchResult(user, true))
                    else
                        if (user.username != AccountService.username)
                            listResult.add(SearchResult(user, false))
                }
                binding.hint.isVisible = listResult.isEmpty()
                adapter.submitList(listResult.toList())
            }
        }

        return binding.root
    }

    companion object {
        fun newInstance() = SearchFragment()
    }

    data class SearchResult (val user: User, val isFriend: Boolean)
}