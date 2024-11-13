package com.example.preparify_jobpreparationapp.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.preparify_jobpreparationapp.Repository.LessonRepository

class LessonViewModelFactory(private val courseId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            return LessonViewModel(courseId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
