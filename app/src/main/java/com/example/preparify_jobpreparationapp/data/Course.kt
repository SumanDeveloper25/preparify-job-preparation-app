package com.example.preparify_jobpreparationapp.data

data class Course(
    var title: String = "",           // The document ID and title are the same
    var category: String = "",
    var createdBy: String = "",
    var creationDate: com.google.firebase.Timestamp? = null,
    var enrolled: Int = 0, // increase the enrolled
    var lastUpdatedBy: String = "",
    var logoUrl: String = "",
    var lessons: List<Lesson> = listOf(),
    val progress: Int = 0
)

