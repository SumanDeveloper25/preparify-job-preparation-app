package com.example.preparify_jobpreparationapp.data

import com.google.firebase.Timestamp

data class Question(
    var courseId: String = "",                // Reference to the related course
    var lessonId: String = "",                // Reference to the related lesson
    var correctAnswers: List<String> = listOf(), // List of correct answers
    var createdAt: Timestamp? = null,
    var createdBy: String = "",
    var explanation: String = "",
    var options: List<String> = listOf(),       // List of options for the question
    var question: String = ""                  // The question text
)

