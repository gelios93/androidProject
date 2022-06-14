package com.example.doyo.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.doyo.R
import com.example.doyo.activities.LoginActivity
import com.example.doyo.activities.ProfileActivity
import com.example.doyo.databinding.FragmentSignupBinding
import com.example.doyo.isValidEmail
import com.example.doyo.isValidName
import com.example.doyo.isValidPassword
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

        binding.username.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.editUsernameSignUp.error = null
        }

        binding.password.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.editPasswordSignUp.error = null
        }

        binding.email.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.editEmailSignUp.error = null
        }

        binding.submitUp.setOnClickListener {
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            val validationUsername = isValidName(username)
            val validationEmail = isValidEmail(email)
            val validationPassword = isValidPassword(password)

            if (validationUsername.result && validationEmail.result && validationPassword.result) {
                val body = HttpService.signUp(parentActivity, username, email, password)
                println(body?.toString(2))
                if (body != null && body.has("code")) {
                    if (body.get("code") == 406) {
                        if (body.get("type") == "username") {
                            binding.editUsernameSignUp.error = "This username is already in use"
                            binding.username.clearFocus()
                        }
                        else {
                            binding.editEmailSignUp.error = "This email is already in use"
                            binding.email.clearFocus()
                        }
                    }
                    else
                        Toast.makeText(parentActivity, body.get("message").toString(), Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(parentActivity, ProfileActivity::class.java).apply {
                        putExtra("data", body.toString())
                    }
                    startActivity(intent)
                    parentActivity.finish()
                }
            }
            else {
                if (!validationUsername.result) {
                    binding.editUsernameSignUp.error = validationUsername.error
                    binding.username.clearFocus()
                }
                if (!validationEmail.result) {
                    binding.editEmailSignUp.error = validationEmail.error
                    binding.email.clearFocus()
                }
                if (!validationEmail.result) {
                    binding.editPasswordSignUp.error = validationPassword.error
                    binding.email.clearFocus()
                }
            }
        }
        return binding.root
    }
}