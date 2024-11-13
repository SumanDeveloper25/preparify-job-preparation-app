package com.example.preparify_jobpreparationapp.viewmodels

import com.example.preparify_jobpreparationapp.Repository.UserRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.preparify_jobpreparationapp.data.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    // LiveData to observe password reset status
    private val _passwordResetStatus = MutableLiveData<Pair<Boolean, String?>>()
    val passwordResetStatus: LiveData<Pair<Boolean, String?>> get() = _passwordResetStatus


    // Method to log in user
    fun loginUser(email: String, password: String, onComplete: (Boolean) -> Unit) {
        userRepository.login(email, password) { success ->
            if (success) {
                // Fetch user details after login
                userRepository.getUserByEmail(email) { userDetails ->
                    _user.value = userDetails // Populate the user LiveData with complete user data
                }
            } else {
                _user.value = null
            }
            onComplete(success)
        }
    }

    // Send password reset email
    fun sendPasswordResetEmail(email: String) {
        userRepository.sendPasswordResetEmail(email) { success, errorMessage ->
            _passwordResetStatus.value = Pair(success, errorMessage)
        }
    }

    // Register new user
    fun registerUser(user: User, password: String) {
        userRepository.register(user, password) { success, registeredUser ->
            _user.value = if (success) registeredUser else null
        }
    }

    // Google Sign-In method
    // UserViewModel changes
    fun signInWithGoogle(account: GoogleSignInAccount, onFirestoreLoaded: (Boolean) -> Unit) {
        userRepository.signInWithGoogle(account) { userModel ->
            _user.value = userModel
            if (userModel != null) {
                // Fetch Firestore data after successful login
                userRepository.loadFirestoreData { firestoreSuccess ->
                    onFirestoreLoaded(firestoreSuccess)
                }
            } else {
                onFirestoreLoaded(false) // Handle login failure
            }
        }
    }


    // Facebook Sign-In method
    fun signInWithFacebook(credential: AuthCredential, email: String?, name: String?) {
        userRepository.signInWithFacebook(credential, email, name) { userModel ->
            _user.value = userModel // directly set the user model
        }
    }

    // Sign out method
    fun signOut() {
        userRepository.signOut()
        _user.value = null
    }
}
