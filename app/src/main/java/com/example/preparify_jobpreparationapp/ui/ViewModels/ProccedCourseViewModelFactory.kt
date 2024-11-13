package com.example.preparify_jobpreparationapp.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.preparify_jobpreparationapp.Repository.CourseRepository

class ProccedCourseViewModelFactory(
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProccedCourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProccedCourseViewModel(courseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
