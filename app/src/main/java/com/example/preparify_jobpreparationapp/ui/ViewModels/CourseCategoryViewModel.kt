//package com.example.preparify_jobpreparationapp.ui.ViewModels
//
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.preparify_jobpreparationapp.data.CourseCategory
//import com.example.preparify_jobpreparationapp.Repository.CourseRepository
//
//class CourseCategoryViewModel(private val courseRepository: CourseRepository) : ViewModel() {
//
//    private val _topCourseCategories = MutableLiveData<List<CourseCategory>>()
//    val topCourseCategories: LiveData<List<CourseCategory>> get() = _topCourseCategories
//
//    private val _error = MutableLiveData<String>()
//    val error: LiveData<String> get() = _error
//
//    // Fetch top 3 course categories from the repository
//    fun fetchTopCourseCategories() {
//        courseRepository.getTop3Categories(
//            onComplete = { categories ->
//                _topCourseCategories.value = categories
//            },
//            onError = { exception ->
//                _topCourseCategories.value = emptyList() // Handle error gracefully
//                Log.e("CourseCategoryViewModel", "Error fetching categories: ${exception.message}")
//            }
//        )
//    }
//}

package com.example.preparify_jobpreparationapp.ui.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.preparify_jobpreparationapp.data.CourseCategory
import com.example.preparify_jobpreparationapp.Repository.CourseRepository

class CourseCategoryViewModel(private val courseRepository: CourseRepository) : ViewModel() {

    private val _topCourseCategories = MutableLiveData<List<CourseCategory>>()
    val topCourseCategories: LiveData<List<CourseCategory>> get() = _topCourseCategories

    private val _error = MutableLiveData<String?>() // Nullable to handle no error
    val error: LiveData<String?> get() = _error

    // Fetch top 3 course categories from the repository
    fun fetchTopCourseCategories() {
        courseRepository.getTop3Categories(
            onComplete = { categories ->
                _topCourseCategories.value = categories
                _error.value = null // Clear any previous errors
            },
            onError = { exception ->
                _topCourseCategories.value = emptyList() // Clear categories on error
                _error.value = exception.message // Provide error message to the UI
                Log.e("CourseCategoryViewModel", "Error fetching categories: ${exception.message}")
            }
        )
    }
}
