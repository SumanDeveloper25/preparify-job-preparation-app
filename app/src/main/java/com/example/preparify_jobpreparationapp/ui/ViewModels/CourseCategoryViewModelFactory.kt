//package com.example.preparify_jobpreparationapp.ui.ViewModels
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.preparify_jobpreparationapp.Repository.CourseRepository
//
//class CourseCategoryViewModelFactory(
//    private val courseRepository: CourseRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(CourseCategoryViewModel::class.java)) {
//            return CourseCategoryViewModel(courseRepository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

package com.example.preparify_jobpreparationapp.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.preparify_jobpreparationapp.Repository.CourseRepository

class CourseCategoryViewModelFactory(
    private val courseRepository: CourseRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CourseCategoryViewModel::class.java) -> {
                CourseCategoryViewModel(courseRepository) as T
            }
            // You can add more ViewModel cases here if needed
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
