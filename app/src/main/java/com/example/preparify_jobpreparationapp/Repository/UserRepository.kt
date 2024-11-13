//package com.example.preparify_jobpreparationapp.Repository
//
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.firestore.FirebaseFirestore
//import com.example.preparify_jobpreparationapp.data.User
//import com.google.firebase.auth.AuthCredential
//
//class UserRepository {
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
//
//    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
//        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                callback(true)
//            } else {
//                callback(false)
//            }
//        }
//    }
//
//    fun loadFirestoreData(onComplete: (Boolean) -> Unit) {
//        // Firestore data fetching logic
//        db.collection("courses")
//            .get()
//            .addOnSuccessListener { documents ->
//                // Process Firestore data
//                onComplete(true)
//            }
//            .addOnFailureListener {
//                onComplete(false) // Firestore data loading failed
//            }
//    }
//
//
//    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, String?) -> Unit) {
//        auth.sendPasswordResetEmail(email)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    onComplete(true, null) // Email sent successfully
//                } else {
//                    onComplete(false, task.exception?.message) // Return error message
//                }
//            }
//    }
//
//    // Function to check if the user is authenticated
//    fun isUserAuthenticated(): Boolean {
//        return auth.currentUser != null
//    }
//
//    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
//        // Fetch user details from the Fire store or Firebase Realtime Database
//        db.collection("users") // Adjust collection name as necessary
//            .whereEqualTo("email", email)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val user = document.toObject(User::class.java)
//                    callback(user) // Return the user object
//                    return@addOnSuccessListener
//                }
//                callback(null) // Return null if no user found
//            }
//            .addOnFailureListener {
//                callback(null) // Handle error and return null
//            }
//    }
//
//    // Register new user using Firebase Auth and save to Fire store
//    fun register(user: User, password: String, callback: (Boolean, User?) -> Unit) {
//        auth.createUserWithEmailAndPassword(user.email, password).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
//                val userToSave = user.copy(id = userId)
//
//                db.collection("users").document(userId).set(userToSave)
//                    .addOnSuccessListener {
//                        callback(true, userToSave)
//                    }
//                    .addOnFailureListener {
//                        callback(false, null)
//                    }
//            } else {
//                callback(false, null)
//            }
//        }
//    }
//
//    // Sign in with Google
//    fun signInWithGoogle(account: GoogleSignInAccount, onComplete: (User?) -> Unit) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
//                    val email = account.email ?: "Unknown"
//                    val name = account.displayName ?: "Unknown"
//                    val profilePicture = account.photoUrl?.toString()
//
//                    val user = User(
//                        id = userId,
//                        name = name,
//                        email = email,
//                        age = 0,
//                        gender = "",
//                        occupation = "",
//                        profilePicture = profilePicture
//                    )
//
//                    // Save user to Firestore if not already present
//                    db.collection("users").document(userId).get()
//                        .addOnSuccessListener { document ->
//                            if (!document.exists()) {
//                                db.collection("users").document(userId).set(user)
//                                    .addOnSuccessListener { onComplete(user) }
//                                    .addOnFailureListener { onComplete(null) }
//                            } else {
//                                onComplete(user) // Return the user if already exists
//                            }
//                        }
//                        .addOnFailureListener {
//                            onComplete(null)
//                        }
//                } else {
//                    onComplete(null)
//                }
//            }
//    }
//
//    // Sign in with Facebook
//    fun signInWithFacebook(
//        credential: AuthCredential,
//        email: String?,
//        name: String?,
//        onComplete: (User?) -> Unit
//    ) {
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
//                    val userEmail = email ?: auth.currentUser?.email ?: "Unknown"
//                    val userName = name ?: auth.currentUser?.displayName ?: "Unknown"
//
//                    val user = User(
//                        id = userId,
//                        name = userName,
//                        email = userEmail,
//                        age = "",
//                        gender = "",
//                        occupation = ""
//                    )
//
//                    // Save user to Firestore if not already present
//                    db.collection("users").document(userId).get()
//                        .addOnSuccessListener { document ->
//                            if (!document.exists()) {
//                                db.collection("users").document(userId).set(user)
//                                    .addOnSuccessListener { onComplete(user) }
//                                    .addOnFailureListener { onComplete(null) }
//                            } else {
//                                onComplete(user) // Return the user if already exists
//                            }
//                        }
//                        .addOnFailureListener {
//                            onComplete(null)
//                        }
//                } else {
//                    onComplete(null)
//                }
//            }
//    }
//
//    // Sign out method
//    fun signOut() {
//        auth.signOut()
//    }
//}

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

    // User login with email and password
    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    // Load Firestore data (e.g., courses)
    fun loadFirestoreData(onComplete: (Boolean) -> Unit) {
        db.collection("courses")
            .get()
            .addOnSuccessListener { documents ->
                // Process Firestore data here if needed
                onComplete(true) // Data fetched successfully
            }
            .addOnFailureListener {
                onComplete(false) // Firestore data loading failed
            }
    }

    // Send password reset email
    fun sendPasswordResetEmail(email: String, onComplete: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null) // Email sent successfully
                } else {
                    onComplete(false, task.exception?.message) // Return error message
                }
            }
    }

    // Check if the user is authenticated
    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    // Get user details by email
    fun getUserByEmail(email: String, callback: (User?) -> Unit) {
        db.collection("users") // Adjust collection name if necessary
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    callback(user) // Return the user object
                    return@addOnSuccessListener
                }
                callback(null) // Return null if no user found
            }
            .addOnFailureListener {
                callback(null) // Handle error and return null
            }
    }

    // Register new user and save to Firestore
    fun register(user: User, password: String, callback: (Boolean, User?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                val userToSave = user.copy(id = userId)

                db.collection("users").document(userId).set(userToSave)
                    .addOnSuccessListener {
                        callback(true, userToSave) // User saved successfully
                    }
                    .addOnFailureListener {
                        callback(false, null) // Error saving user
                    }
            } else {
                callback(false, null) // Registration failed
            }
        }
    }

    // Sign in with Google
    fun signInWithGoogle(account: GoogleSignInAccount, onComplete: (User?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val email = account.email ?: "Unknown"
                    val name = account.displayName ?: "Unknown"
                    val profilePicture = account.photoUrl?.toString()

                    val user = User(
                        id = userId,
                        name = name,
                        email = email,
                        age = "",
                        gender = "",
                        occupation = "",
                        profilePicture = profilePicture
                    )

                    // Save user to Firestore if not already present
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (!document.exists()) {
                                db.collection("users").document(userId).set(user)
                                    .addOnSuccessListener { onComplete(user) }
                                    .addOnFailureListener { onComplete(null) }
                            } else {
                                onComplete(user) // Return the user if already exists
                            }
                        }
                        .addOnFailureListener {
                            onComplete(null)
                        }
                } else {
                    onComplete(null)
                }
            }
    }

    // Sign in with Facebook
    fun signInWithFacebook(
        credential: AuthCredential,
        email: String?,
        name: String?,
        onComplete: (User?) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userEmail = email ?: auth.currentUser?.email ?: "Unknown"
                    val userName = name ?: auth.currentUser?.displayName ?: "Unknown"

                    val user = User(
                        id = userId,
                        name = userName,
                        email = userEmail,
                        age = "",
                        gender = "",
                        occupation = ""
                    )

                    // Save user to Firestore if not already present
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (!document.exists()) {
                                db.collection("users").document(userId).set(user)
                                    .addOnSuccessListener { onComplete(user) }
                                    .addOnFailureListener { onComplete(null) }
                            } else {
                                onComplete(user) // Return the user if already exists
                            }
                        }
                        .addOnFailureListener {
                            onComplete(null)
                        }
                } else {
                    onComplete(null)
                }
            }
    }

    // Sign out method
    fun signOut() {
        auth.signOut()
    }
}

