package com.example.doyo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.doyo.R
import com.example.doyo.activities.LoginActivity
import com.example.doyo.activities.ProfileActivity
import com.example.doyo.databinding.FragmentSignupBinding
import com.example.doyo.services.HttpService

class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var parentActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = activity as LoginActivity
        val binding = FragmentSignupBinding.inflate(layoutInflater, container, false)

        binding.toIn.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignupToSignin()
            view?.findNavController()?.navigate(action)
        }

        binding.submitUp.setOnClickListener {
            val body = HttpService.signUp(parentActivity, binding.editUsernameSignUp.text.toString(), binding.editEmailSignUp.text.toString(), binding.editPasswordSignUp.text.toString())

            if(body != null && body.has("code")) {
                Toast.makeText(parentActivity, body.get("message").toString(), Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(parentActivity, ProfileActivity::class.java).apply {
                    putExtra("data", body.toString())
                }
                startActivity(intent)
                parentActivity.finish()
            }

        }
        return binding.root
    }
}