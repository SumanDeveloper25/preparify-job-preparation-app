package com.example.preparify_jobpreparationapp.Repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.example.preparify_jobpreparationapp.data.User
import com.google.firebase.auth.AuthCredential

class UserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getAuthUserName(callback: (String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(null)
            return
        }
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name")
                callback(name)
            }
            .addOnFailureListener { callback(null) }
    }

    fun getAuthUserId(): String? = auth.currentUser?.uid

    fun isUserAuthenticated(): Boolean = auth.currentUser != null

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    fun isEnrolledInAnyCourse(callback: (Boolean) -> Unit) {
        getAuthUserId()?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val courses = document.get("enrolledCourses") as? List<*>
                    callback(!courses.isNullOrEmpty())
                }
                .addOnFailureListener { callback(false) }
        } ?: callback(false)
    }

    fun loadFirestoreData(onComplete: (Boolean) -> Unit) {
        db.collection("courses").get()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            onComplete(task.isSuccessful, task.exception?.message)
        }
    }

    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
        db.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                val user = documents.firstOrNull()?.toObject(User::class.java)
                callback(user)
            }
            .addOnFailureListener { callback(null) }
    }

    fun register(user: User, password: String, callback: (Boolean, User?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                val userToSave = user.copy(id = userId)
                saveUserToFirestore(userToSave, callback)
            } else {
                callback(false, null)
            }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount, onComplete: (User?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                val user = User(
                    id = userId,
                    name = account.displayName ?: "Unknown",
                    email = account.email ?: "Unknown",
                    profilePicture = account.photoUrl?.toString()
                )
                checkAndSaveUser(user, onComplete)
            } else {
                onComplete(null)
            }
        }
    }

    fun signInWithFacebook(
        credential: AuthCredential,
        email: String?,
        name: String?,
        onComplete: (User?) -> Unit
    ) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                val user = User(
                    id = userId,
                    name = name ?: auth.currentUser?.displayName ?: "Unknown",
                    email = email ?: auth.currentUser?.email ?: "Unknown"
                )
                checkAndSaveUser(user, onComplete)
            } else {
                onComplete(null)
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private fun saveUserToFirestore(user: User, callback: (Boolean, User?) -> Unit) {
        db.collection("users").document(user.id).set(user)
            .addOnSuccessListener { callback(true, user) }
            .addOnFailureListener { callback(false, null) }
    }

    private fun checkAndSaveUser(user: User, onComplete: (User?) -> Unit) {
        db.collection("users").document(user.id).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    saveUserToFirestore(user) { success, savedUser ->
                        onComplete(if (success) savedUser else null)
                    }
                } else {
                    onComplete(user)
                }
            }
            .addOnFailureListener { onComplete(null) }
    }
}
