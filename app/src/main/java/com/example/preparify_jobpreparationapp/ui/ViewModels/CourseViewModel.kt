package com.example.preparify_jobpreparationapp.ui.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.preparify_jobpreparationapp.Repository.CourseRepository
import com.example.preparify_jobpreparationapp.data.Course

class CourseViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _courses = MutableLiveData<List<Course>>()
    private val _latestCourses = MutableLiveData<List<Course>>()
    private val _trendingCourses = MutableLiveData<List<Course>>()

    val trendingCourses: LiveData<List<Course>> get() = _trendingCourses
    val courses: LiveData<List<Course>> get() = _courses
    val latestCourses: LiveData<List<Course>> get() = _latestCourses

    // Fetch top courses
    fun fetchTopVCourses() {
        repository.getTopCourses(
            onComplete = { courseList ->
                _courses.value = courseList
            },
            onError = { exception ->
                Log.e("CourseViewModel", "Error fetching courses", exception)
                _courses.value = emptyList() // Optionally handle error state
            }
        )
    }

    fun fetchCoursesByCategory(categoryName: String) {
        repository.fetchAllCoursesByCategory(categoryName,
            onComplete = { courseList ->
                // Get the total number of courses
                val totalCourses = courseList.size

                // Sort for latest courses (by creationDate)
                val latestCount = when {
                    totalCourses <= 1 -> totalCourses
                    totalCourses in 2..5 -> 3
                    else -> totalCourses - 5
                }.coerceAtLeast(0) // Ensure count is not less than 0

                val latest = courseList
                    .sortedByDescending { it.creationDate?.toDate() } // Convert Timestamp to Date for sorting
                    .take(latestCount)

                _latestCourses.postValue(latest)

                // Create a list of trending courses without repeating the latest ones
                val trendingCoursesList = courseList.filterNot { course -> latest.contains(course) }
                    .sortedByDescending { it.enrolled }

                // Determine how many trending courses to show
                val popular = when {
                    totalCourses <= 1 -> trendingCoursesList.take(1)
                    totalCourses in 2..5 -> trendingCoursesList // Show all trending if 2 to 5
                    else -> trendingCoursesList.take(5) // Show top 5 trending courses if more than 5
                }

                _trendingCourses.postValue(popular)
            },
            onError = { exception ->
                Log.e("CourseViewModel", "Error fetching courses by category", exception)
                _latestCourses.value = emptyList() // Optionally handle error state
                _trendingCourses.value = emptyList() // Handle error state for trending courses
            }
        )
    }
}
