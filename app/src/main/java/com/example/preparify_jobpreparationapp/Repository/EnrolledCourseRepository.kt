package com.example.preparify_jobpreparationapp.Repository

import com.example.preparify_jobpreparationapp.data.Course
import com.example.preparify_jobpreparationapp.data.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.android.gms.tasks.Task

class EnrolledCourseRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUser = auth.currentUser
    private val userId = currentUser?.uid
    private val userRef = userId?.let { firestore.collection("users").document(it) }

    fun fetchCourseData(courseTitle: String): Task<Course> {
        // Query the courses collection using the title
        val courseRef = firestore.collection("courses")
            .whereEqualTo("title", courseTitle)
            .get()

        // Create a Task to return a single Course object
        return courseRef.continueWith { task ->
            if (task.isSuccessful && task.result != null && task.result.documents.isNotEmpty()) {
                val courseSnapshot = task.result.documents.first()
                courseSnapshot.toObject(Course::class.java) ?: throw Exception("Course data is null")
            } else {
                throw Exception("Course not found")
            }
        }
    }

    fun enrollInCourse(course: Course, onResult: (String) -> Unit) {
        if (currentUser != null) {
            // Store only the title in the enrolledCourses list for the user
            userRef?.update("enrolledCourses", FieldValue.arrayUnion(course.title))
                ?.addOnSuccessListener {
                    // Once user enrollment is successful, update the course count
                    updateCourseCount(course.title, onResult)
                }
                ?.addOnFailureListener { e ->
                    onResult("Failed to enroll: ${e.message}")
                }
        } else {
            onResult("User not logged in")
        }
    }

    private fun updateCourseCount(courseTitle: String, onResult: (String) -> Unit) {
        // Reference to the course document
        val courseRef = firestore.collection("courses").document(courseTitle)

        // Start a Firestore transaction to increment the 'enrolled' field
        firestore.runTransaction { transaction ->
            val courseSnapshot = transaction.get(courseRef)

            // Check if the course document exists
            if (!courseSnapshot.exists()) {
                throw Exception("Course not found")
            }

            // Get the current value of 'enrolled' field and increment it
            val currentEnrollment = courseSnapshot.getLong("enrolled") ?: 0
            transaction.update(courseRef, "enrolled", currentEnrollment + 1)
        }.addOnSuccessListener {
            // Enrollment successful, return success message
            onResult("Successfully enrolled in $courseTitle")
        }.addOnFailureListener { e ->
            // Handle any errors that occurred during the transaction
            onResult("Failed to update course enrollment count: ${e.message}")
        }
    }

    fun getUserEnrolledCourses(userId: String, callback: (List<Course>?, String?) -> Unit) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let { user ->
                        val courseList = mutableListOf<Course>()
                        val enrolledCourseTitles = user.enrolledCourses

                        if (enrolledCourseTitles.isEmpty()) {
                            // If there are no enrolled courses, return an empty list
                            callback(emptyList(), null)
                            return@let
                        }

                        // Create a list to hold the tasks
                        val tasks = mutableListOf<Task<Course>>()

                        for (title in enrolledCourseTitles) {
                            // Fetch each course and add the task to the list
                            val task = fetchCourseData(title)
                            tasks.add(task)
                        }

                        // Use Tasks.whenAllComplete to wait for all fetches to complete
                        Tasks.whenAllComplete(tasks).addOnCompleteListener {
                            // All tasks completed
                            for (task in tasks) {
                                if (task.isSuccessful) {
                                    task.result?.let { course ->
                                        courseList.add(course)
                                    }
                                } else {
                                    // Handle individual task failure if needed
                                    // You might log or collect error messages if necessary
                                }
                            }
                            // Return the collected courses
                            callback(courseList, null)
                        }
                    } ?: callback(null, "User data is null")
                } else {
                    callback(null, "User not found")
                }
            }
            .addOnFailureListener { exception ->
                callback(null, "Error fetching user courses: ${exception.message}")
            }
    }
}

