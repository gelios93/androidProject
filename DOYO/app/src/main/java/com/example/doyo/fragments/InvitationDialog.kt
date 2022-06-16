package com.example.doyo.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doyo.adapters.InviteFriendsListAdapter
import com.example.doyo.databinding.DialogInvitationBinding
import com.example.doyo.services.AccountService
import com.example.doyo.services.SocketService
import org.json.JSONObject

class InvitationDialog: DialogFragment()  {

    private lateinit var inviteFriendsList: RecyclerView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val binding = DialogInvitationBinding.inflate(layoutInflater)

            inviteFriendsList = binding.inviteFriends

            val dial = AlertDialog.Builder(it)
                .setView(binding.root)
                .create()

            val inviteFriendsAdapter = InviteFriendsListAdapter(layoutInflater, dial)
            inviteFriendsList.adapter = inviteFriendsAdapter
            inviteFriendsList.setHasFixedSize(true)
            inviteFriendsList.layoutManager = LinearLayoutManager(context)
            inviteFriendsAdapter.submitList(AccountService.friends)

            dial
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}