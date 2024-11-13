package com.example.preparify_jobpreparationapp.data

//data class CourseCategory(
//    val name: String = "", // This will map to the category name
//    val courseCount: Int = 0, // Use camelCase for consistency with other fields
//    val categoryImage: String = ""
//)

data class CourseCategory(
    var courseCount: Int = 0,
    var name: String = "",
    val categoryImage: String = ""
)
