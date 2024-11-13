package com.example.preparify_jobpreparationapp.data

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    var age: String = "",
    val gender: String = "",
    val occupation: String = "",
    val profilePicture: String? = "",
    val enrolledCourses: List<String> = emptyList()
)


