package com.example.preparify_jobpreparationapp.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.preparify_jobpreparationapp.Repository.CourseRepository

class ProccedCourseViewModel(private val courseRepository: CourseRepository) : ViewModel() {
    private val _courseData = MutableLiveData<Map<String, Any>?>()
    val courseData: LiveData<Map<String, Any>?> get() = _courseData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchCourseDetails(courseId: String) {
        _isLoading.value = true
        courseRepository.getCourseDetails(courseId,
            onComplete = { courseDetails ->
                _isLoading.value = false
                if (courseDetails != null) {
                    _courseData.value = courseDetails
                } else {
                    _errorMessage.value = "No Course Found"
                }
            },
            onError = { exception ->
                _isLoading.value = false
                _errorMessage.value = "Error: ${exception.message}"
            }
        )
    }
}
