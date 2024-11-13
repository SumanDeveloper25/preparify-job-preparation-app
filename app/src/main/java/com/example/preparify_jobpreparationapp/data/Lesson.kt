package com.example.preparify_jobpreparationapp.data

import com.google.firebase.Timestamp

data class Lesson(
    val lessonId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val lessonNo: Int = 0,
    val content: String = "",
    val createdBy: String = "",
    val created_at: Timestamp? = null, // Nullable to handle missing data
    val videoUrl: String? = null, // Nullable if the video URL is not always provided
    var isCompleted: Boolean = false
)
