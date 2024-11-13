package com.example.preparify_jobpreparationapp.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.preparify_jobpreparationapp.Repository.EnrolledCourseRepository

class EnrolledCourseViewModelFactory(private val repository: EnrolledCourseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnrolledCourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EnrolledCourseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
