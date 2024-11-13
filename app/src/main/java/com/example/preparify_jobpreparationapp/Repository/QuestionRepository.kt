package com.example.preparify_jobpreparationapp.Repository

import com.example.preparify_jobpreparationapp.data.Question
import com.google.firebase.firestore.FirebaseFirestore

class QuestionRepository {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Add a new question
    fun addQuestion(question: Question, onComplete: (String) -> Unit, onError: (String) -> Unit) {
        // Generate a new document ID for the question
        val questionRef = firestore.collection("questions").document()

        // Set the question object with the generated ID
        questionRef.set(question.copy(createdAt = com.google.firebase.Timestamp.now()))
            .addOnSuccessListener { onComplete("Question added successfully") }
            .addOnFailureListener { e -> onError("Failed to add question: ${e.message}") }
    }

    // Fetch questions by course and lesson IDs
    fun fetchQuestions(courseId: String, lessonId: String, onComplete: (List<Question>) -> Unit, onError: (String) -> Unit) {
        firestore.collection("questions")
            .whereEqualTo("courseId", courseId)
            .whereEqualTo("lessonId", lessonId)
            .get()
            .addOnSuccessListener { documents ->
                val questionsList = documents.mapNotNull { document ->
                    document.toObject(Question::class.java).apply {
                        // Populate createdAt with the timestamp from Firestore if available
                        createdAt = document.getTimestamp("createdAt") // Make sure to match the field name
                    }
                }
                onComplete(questionsList) // Return the list of questions
            }
            .addOnFailureListener { e -> onError("Failed to fetch questions: ${e.message}") }
    }
}
