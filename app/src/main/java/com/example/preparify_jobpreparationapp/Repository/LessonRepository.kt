package com.example.preparify_jobpreparationapp.Repository

import com.example.preparify_jobpreparationapp.data.Lesson
import com.example.preparify_jobpreparationapp.data.Question
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class LessonRepository {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Fetch lessons for a specific course and order them by createdAt
    fun fetchLessonsForCourse(courseId: String, onComplete: (List<Lesson>) -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("courses")
            .document(courseId)
            .collection("lessons")
            .orderBy("created_at")
            .get()
            .addOnSuccessListener { documents ->
                val lessons = documents.toLessonsList() // Convert QuerySnapshot to List<Lesson>
                onComplete(lessons)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }


    // Fetch questions for a specific lesson
    fun fetchQuestionsForLesson(
        lessonId: String,
        onComplete: (List<Question>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("questions")
            .whereEqualTo("belongLesson", lessonId) // Assuming the field in Firestore to link questions to lessons is 'belongLesson'
            .get()
            .addOnSuccessListener { documents ->
                val questions = documents.toQuestionsList() // Convert QuerySnapshot to List<Question>
                onComplete(questions)
            }
            .addOnFailureListener { exception ->
                onError(exception) // Return the exception on failure
            }
    }

    // Helper function to convert QuerySnapshot to List<Lesson>
    private fun QuerySnapshot.toLessonsList(): List<Lesson> {
        return this.documents.mapNotNull { document ->
            document.toObject(Lesson::class.java)?.apply {
                // Optionally manipulate data here if needed
            }
        }
    }

    // Helper function to convert QuerySnapshot to List<Question>
    private fun QuerySnapshot.toQuestionsList(): List<Question> {
        return this.documents.mapNotNull { document ->
            document.toObject(Question::class.java)?.apply {
                // Optionally manipulate data here if needed
            }
        }
    }

    // Fetch lesson content from a specific lesson document under the "lessons" sub-collection of the "courses" collection
    fun fetchLessonContent(
        courseId: String, // Need to pass courseId as it's used to find the lesson in the correct course
        lessonId: String,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("courses")  // Target the course collection
            .document(courseId)  // Specify the course document
            .collection("lessons")  // Specify the "lessons" sub-collection
            .document(lessonId)  // Get the specific lesson document
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Map the document to a Lesson object
                    val lesson = document.toObject(Lesson::class.java)
                    lesson?.content?.let { content ->
                        onSuccess(content) // Return the content field
                    } ?: onError(Exception("Content is missing in the lesson document"))
                } else {
                    onError(Exception("Lesson not found"))
                }
            }
            .addOnFailureListener { exception ->
                onError(exception) // Return any error encountered
            }
    }

//    fun fetchLessonQuestions(courseId: String, lessonId: String) {
//        firestore.collection("courses")
//            .document(courseId)
//            .collection("lessons")
//            .document(lessonId)
//            .get()
//    }

    // Fetch a lesson by ID from the lessons sub-collection
    fun fetchLessonById(
        courseId: String,
        lessonId: String,
        onSuccess: (Lesson?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        firestore.collection("courses")  // Access "courses" collection
            .document(courseId)         // Target specific course document
            .collection("lessons")      // Navigate to the "lessons" sub-collection
            .document(lessonId)         // Fetch specific lesson document
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val lesson = document.toObject(Lesson::class.java)
                    onSuccess(lesson) // Return the mapped Lesson object
                } else {
                    onError(Exception("Lesson not found"))
                }
            }
            .addOnFailureListener { exception ->
                onError(exception) // Handle errors
            }
    }
}
