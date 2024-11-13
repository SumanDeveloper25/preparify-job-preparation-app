package com.example.preparify_jobpreparationapp.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.Repository.UserRepository
import com.example.preparify_jobpreparationapp.ui.ViewModels.ViewModelFactory
import com.example.preparify_jobpreparationapp.viewmodels.UserViewModel
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import org.json.JSONException
import android.accounts.Account
import android.accounts.AccountManager

class LoginOptionsFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userViewModel: UserViewModel
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private lateinit var callbackManager: CallbackManager
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login_options, container, false)
        userRepository = UserRepository()

        // Initialize Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Initialize ViewModel with UserRepository
        userViewModel = ViewModelProvider(this, ViewModelFactory(userRepository))[UserViewModel::class.java]

        // Set up Google Sign-In launcher
        signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        // Facebook Login initialization
        callbackManager = CallbackManager.Factory.create()

        // Set up Google and Facebook login buttons
        val googleLoginButton: Button = view.findViewById(R.id.btnGoogleLogin)
        val facebookLoginButton: Button = view.findViewById(R.id.btnFacebookLogin)
        val emailLoginButton: Button = view.findViewById(R.id.btnEmailLogin)
        val registerButton: TextView = view.findViewById(R.id.tvSignUp)
        val doLaterButton: LinearLayout = view.findViewById(R.id.btnDoLater)

        // Check if the user is already authenticated
        if (userRepository.isUserAuthenticated()) {
            // If authenticated, navigate directly to the course categories
            findNavController().navigate(R.id.action_loginOptionsFragment_to_courseCategoryFragment)
        }

        // Google login button click
        googleLoginButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }

        // Email login button click
        emailLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginOptionsFragment_to_loginFragment)
        }

        // Sign-up button
        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginOptionsFragment_to_registrationFragment)
        }

        // Facebook login button click
        facebookLoginButton.setOnClickListener {
            removeFacebookAccounts()
            LoginManager.getInstance().logOut() // Log out of any existing Facebook sessions
            LoginManager.getInstance().logInWithReadPermissions(
                this, callbackManager, listOf("email", "public_profile")
            )
        }

        // Facebook login callback
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.d("FacebookLogin", "Login canceled.")
            }

            override fun onError(error: FacebookException) {
                Log.d("FacebookLogin", "Login failed: ${error.message}")
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
                Log.d("FacebookLogin", "Login Success: $result")
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Override the back button to close the app
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity() // Close the app
            }
        })
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            userViewModel.signInWithGoogle(account) { firestoreSuccess ->
                if (firestoreSuccess) {
                    // Navigate to CourseCategoryFragment once Firestore data is loaded
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isAdded && !isDetached) {
                            findNavController().navigate(R.id.action_loginOptionsFragment_to_courseCategoryFragment)
                        }
                    }, 0.4.toLong()) // Small delay to ensure proper data loading
                } else {
                    Log.e("Firestore", "Failed to load Firestore data")
                }
            }
        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
        }
    }


    private fun handleFacebookAccessToken(token: AccessToken?) {
        token?.let {
            val request = GraphRequest.newMeRequest(token) { jsonObject, _ ->
                try {
                    val email = jsonObject?.getString("email")
                    val name = jsonObject?.getString("name")

                    val credential = FacebookAuthProvider.getCredential(token.token)
                    userViewModel.signInWithFacebook(credential, email, name)
                    findNavController().navigate(R.id.action_loginOptionsFragment_to_courseCategoryFragment)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            val parameters = Bundle()
            parameters.putString("fields", "id,name,email")
            request.parameters = parameters
            request.executeAsync()
        }
    }

    private fun removeFacebookAccounts() {
        val accountManager = AccountManager.get(requireContext())
        val accounts: Array<Account> = accountManager.getAccountsByType("com.facebook.auth.login")

        for (account in accounts) {
            accountManager.removeAccountExplicitly(account)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Handle Facebook login result
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
