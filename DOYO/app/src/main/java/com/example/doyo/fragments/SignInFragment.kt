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
import com.example.doyo.databinding.FragmentSigninBinding
import com.example.doyo.services.HttpService
import com.example.doyo.services.SocketService

class SignInFragment : Fragment(R.layout.fragment_signin) {

    private lateinit var parentActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parentActivity = activity as LoginActivity
        submit()
        val binding = FragmentSigninBinding.inflate(layoutInflater, container, false)

        val emailText = binding.email
        val passwordText = binding.password

        emailText.setText(parentActivity.email)
        passwordText.setText(parentActivity.password)

        binding.toUp.setOnClickListener {
            val action = SignInFragmentDirections.actionSigninToSignup()
            view?.findNavController()?.navigate(action)
            parentActivity.email = emailText.text.toString()
            parentActivity.password = passwordText.text.toString()
        }

        binding.email.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.editEmailSignIn.error = null
        }

        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.editPasswordSignIn.error = null
        }

        binding.submitIn.setOnClickListener {
            if (emailText.text.toString() != "" && passwordText.text.toString() != "")
                submit(emailText.text.toString(), passwordText.text.toString())
            else {
                if (emailText.text.toString() == "") {
                    binding.editEmailSignIn.error = "This field must be filled"
                    emailText.clearFocus()
                }
                if (passwordText.text.toString() == "") {
                    binding.editPasswordSignIn.error = "This field must be filled"
                    passwordText.clearFocus()
                }
            }
        }

        return binding.root
    }

    private fun submit(email: String = " ", password: String = " ") {
        val body = HttpService.signIn(parentActivity, email, password)
        if (body != null){
            if(body.has("code")) {
                Toast.makeText(parentActivity, body.get("message").toString(), Toast.LENGTH_SHORT).show()
            }
            else {
                SocketService.socket.off()
                val intent = Intent(parentActivity, ProfileActivity::class.java)
                startActivity(intent)
                parentActivity.finish()
            }
        }
    }
}