package com.example.preparify_jobpreparationapp.Repository

import com.example.preparify_jobpreparationapp.data.Course
import com.example.preparify_jobpreparationapp.data.Question
import com.example.preparify_jobpreparationapp.data.User
import com.example.preparify_jobpreparationapp.data.CourseCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CourseRepository {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }


    fun fetchAllCoursesByCategory(
        categoryName: String,
        onComplete: (List<Course>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("courses")
            .whereEqualTo("category", categoryName) // Assuming you have a 'category' field in your Course documents
            .get()
            .addOnSuccessListener { documents ->
                val courseList = documents.mapNotNull { document ->
                    document.toObject(Course::class.java)
                }
                onComplete(courseList) // Return the list of courses in the specified category
            }
            .addOnFailureListener { exception ->
                onError(exception) // Handle any errors
            }
    }

    // Fetch course details by course ID
    fun getCourseDetails(courseId: String, onComplete: (Map<String, Any>?) -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("courses").document(courseId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onComplete(document.data) // Return the course details as a map
                } else {
                    onComplete(null) // No course found
                }
            }
            .addOnFailureListener { exception ->
                onError(exception) // Handle error
            }
    }

    // Enroll in a course
    fun enrollInCourse(courseId: String, onResult: (String) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = firestore.collection("users").document(userId)
            val courseRef = firestore.collection("courses").document(courseId)

            // First, fetch the full Course object
            courseRef.get()
                .addOnSuccessListener { courseDocument ->
                    if (courseDocument.exists()) {
                        val course = courseDocument.toObject(Course::class.java)

                        if (course != null) {
                            // Now fetch the user data
                            userRef.get()
                                .addOnSuccessListener { userDocument ->
                                    if (userDocument.exists()) {
                                        val userData = userDocument.toObject(User::class.java) ?: User()
                                        val enrolledCourses = userData.enrolledCourses.toMutableList()
                                            ?: mutableListOf()

                                        // Check if already enrolled in this course
                                        if (!enrolledCourses.contains(course.title)) { // Check by title
                                            enrolledCourses.add(course.title) // Add course title only

                                            userRef.update("enrolledCourses", enrolledCourses)
                                                .addOnSuccessListener {
                                                    onResult("Successfully enrolled in course")
                                                }
                                                .addOnFailureListener { e ->
                                                    onResult("Failed to enroll: ${e.message}")
                                                }
                                        } else {
                                            onResult("Already enrolled in this course")
                                        }
                                    } else {
                                        // If the user doesn't exist, create a new user with the enrolled course
                                        val newUserData = User(
                                            id = userId,
                                            name = "",
                                            email = "",
                                            enrolledCourses = mutableListOf(course.title) // Add the course title here
                                        )
                                        userRef.set(newUserData)
                                            .addOnSuccessListener {
                                                onResult("Successfully enrolled in course")
                                            }
                                            .addOnFailureListener { e ->
                                                onResult("Failed to enroll: ${e.message}")
                                            }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    onResult("Error fetching user data: ${e.message}")
                                }
                        } else {
                            onResult("Course not found")
                        }
                    } else {
                        onResult("Course not found")
                    }
                }
                .addOnFailureListener { e ->
                    onResult("Error fetching course: ${e.message}")
                }
        } else {
            onResult("User not logged in")
        }
    }

    // Fetch top 3 course categories
    fun getTop3Categories(onComplete: (List<CourseCategory>) -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("course-category")
            .get()
            .addOnSuccessListener { documents ->
                val categories = documents.mapNotNull { document ->
                    document.toObject(CourseCategory::class.java)
                }
                onComplete(categories)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    // Fetch top courses
    fun getTopCourses(onComplete: (List<Course>) -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("courses")
            .orderBy("enrolled", com.google.firebase.firestore.Query.Direction.DESCENDING) // Order by enrollment
            .limit(10) // Get the top 10 courses
            .get()
            .addOnSuccessListener { documents ->
                val courseList = documents.mapNotNull { document ->
                    document.toObject(Course::class.java)
                }
                onComplete(courseList)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }

    // Fetch questions related to a specific course and lesson
    fun getQuestionsForLesson(courseId: String, lessonId: String, onComplete: (List<Question>) -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("questions")
            .whereEqualTo("courseId", courseId)
            .whereEqualTo("lessonId", lessonId)
            .get()
            .addOnSuccessListener { documents ->
                val questionsList = documents.mapNotNull { document ->
                    document.toObject(Question::class.java)
                }
                onComplete(questionsList)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}
