package com.example.preparify_jobpreparationapp.Repository

import com.example.preparify_jobpreparationapp.data.Lesson
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class LessonRepository {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Fetch lessons for a specific course and order them by createdAt
    fun fetchLessonsForCourse(
        courseId: String,
        onComplete: (List<Lesson>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("courses")
            .document(courseId)
            .collection("lessons")
            .orderBy("created_at")  // Order lessons by the 'createdAt' field in ascending order
            .get()
            .addOnSuccessListener { documents ->
                val lessonList = documents.toLessonsList() // Convert QuerySnapshot to List<Lesson>
                onComplete(lessonList) // Return the list of lessons on success
            }
            .addOnFailureListener { exception ->
                onError(exception) // Return the exception on failure
            }
    }

    // Helper extension function to convert QuerySnapshot to a list of Lesson objects
    private fun QuerySnapshot.toLessonsList(): List<Lesson> {
        return this.documents.mapNotNull { document ->
            document.toObject(Lesson::class.java)?.apply {
                // If you need to manipulate or log data, you can do it here
            }
        }
    }
}
