package com.example.preparify_jobpreparationapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.preparify_jobpreparationapp.Repository.LessonRepository

class LessonViewModelFactory(private val lessonRepository: LessonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LessonViewModel(lessonRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
