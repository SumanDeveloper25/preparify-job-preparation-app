package com.example.preparify_jobpreparationapp.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.preparify_jobpreparationapp.Repository.EnrolledCourseRepository
import com.example.preparify_jobpreparationapp.data.Course
import com.example.preparify_jobpreparationapp.data.User

class EnrolledCourseViewModel(private val repository: EnrolledCourseRepository) : ViewModel() {

    private val _enrollmentStatus = MutableLiveData<String>()
    val enrollmentStatus: LiveData<String> get() = _enrollmentStatus

    private val _enrolledCourses = MutableLiveData<List<Course>?>()
    val enrolledCourses: LiveData<List<Course>?> get() = _enrolledCourses

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Method to fetch course data based on title with success and failure callbacks
    fun fetchCourseData(courseTitle: String, onSuccess: (Course) -> Unit, onFailure: (String) -> Unit) {
        repository.fetchCourseData(courseTitle)
            .addOnSuccessListener { course ->
                // On success, call the success callback
                onSuccess(course)
            }
            .addOnFailureListener { exception ->
                // On failure, call the failure callback
                onFailure(exception.message ?: "Unknown error")
            }
    }

    // Method to enroll in a course based on Course object
    fun enrollInCourse(course: Course) { // Change here to accept Course
        repository.enrollInCourse(course) { result -> // Adjust the repository call
            _enrollmentStatus.postValue(result)
        }
    }


    // Fetch the user's enrolled courses
    fun fetchUserCourses(userId: String) { // Accept userId as a parameter
        repository.getUserEnrolledCourses(userId) { courses, error ->
            if (error != null) {
                _errorMessage.postValue(error)
            } else {
                _enrolledCourses.postValue(courses)
            }
        }
    }
}
