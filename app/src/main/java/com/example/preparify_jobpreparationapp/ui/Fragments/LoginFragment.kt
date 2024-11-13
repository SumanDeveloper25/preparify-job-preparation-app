package com.example.preparify_jobpreparationapp.ui.Fragments

import com.example.preparify_jobpreparationapp.Repository.UserRepository
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.viewmodels.UserViewModel
import com.example.preparify_jobpreparationapp.viewmodels.UserViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var resetPassword: TextView
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Initialize the repository and ViewModel
        val userRepository = UserRepository() // Assuming you have a way to instantiate this repository
        val factory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        // Bind UI elements with IDs
        emailInputLayout = view.findViewById(R.id.emailInputLayout)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordInputLayout = view.findViewById(R.id.passwordInputLayout)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)
        progressBar = view.findViewById(R.id.progressBar)
        resetPassword = view.findViewById(R.id.forgotPassword)

        // Set login button click listener
        loginButton.setOnClickListener {
            loginUser()
        }

        resetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_passwordResetFragment)
        }
        // Observe login result
        userViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                // Navigate to HomeFragment
//                findNavController().navigate(R.id.action_loginFragment_to_courseCategoryFragment)
            } else {
                Toast.makeText(context, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
            }
            progressBar.visibility = View.GONE // Hide the progress bar after login attempt
        }

        return view
    }
    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Enter a valid email"
            return
        } else {
            emailInputLayout.error = null
        }

        if (password.isEmpty()) {
            passwordInputLayout.error = "Enter your password"
            return
        } else {
            passwordInputLayout.error = null
        }

        // Show the progress bar
        progressBar.visibility = View.VISIBLE

        // Pass data to ViewModel for login
        userViewModel.loginUser(email, password) { success ->
            progressBar.visibility = View.GONE // Hide the progress bar after login attempt

            if (success) {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                // Navigate to HomeFragment
                findNavController().navigate(R.id.action_loginFragment_to_courseCategoryFragment)
            } else {
                Toast.makeText(context, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
