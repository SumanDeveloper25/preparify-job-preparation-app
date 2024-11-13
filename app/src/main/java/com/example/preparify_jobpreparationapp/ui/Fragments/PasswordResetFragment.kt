package com.example.preparify_jobpreparationapp.ui.Fragments

import com.example.preparify_jobpreparationapp.Repository.UserRepository
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.viewmodels.UserViewModel
import com.example.preparify_jobpreparationapp.viewmodels.UserViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class PasswordResetFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var emailEditText: TextInputEditText
    private lateinit var resetButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_password_reset, container, false)

        // Initialize ViewModel
        val userRepository = UserRepository() // Ensure com.example.preparify_jobpreparationapp.Repository.UserRepository is properly implemented
        val factory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)

        // Bind UI elements
        emailEditText = view.findViewById(R.id.emailEditText)
        resetButton = view.findViewById(R.id.otpSubmitBtn)
        progressBar = view.findViewById(R.id.progressBar)

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                resetButton.isEnabled = false
                userViewModel.sendPasswordResetEmail(email)
            } else {
                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe password reset status
        userViewModel.passwordResetStatus.observe(viewLifecycleOwner) { status ->
            progressBar.visibility = View.GONE
            resetButton.isEnabled = true
            if (status.first) {
                // Open Gmail app
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_EMAIL)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK // Open the email app directly
                }
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    // If no email app is found, display a Toast message
                    Toast.makeText(context, "No email app found!", Toast.LENGTH_SHORT).show()
                }

                // Navigate to LoginFragment in the background
                findNavController().navigate(R.id.action_passwordResetFragment_to_loginFragment)
            } else {
                Toast.makeText(context, "Failed to send reset email: ${status.second}", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
