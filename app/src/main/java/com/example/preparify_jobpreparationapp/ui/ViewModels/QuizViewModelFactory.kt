package com.example.preparify_jobpreparationapp.ui.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.preparify_jobpreparationapp.Repository.UserRepository

class QuizViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(UserRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

