package com.example.preparify_jobpreparationapp.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.preparify_jobpreparationapp.Repository.LessonRepository
import com.example.preparify_jobpreparationapp.data.Lesson

class LessonViewModel(private val courseId: String) : ViewModel() {

    private val lessonRepository = LessonRepository()
    private val _lessons = MutableLiveData<List<Lesson>>()
    val lessons: LiveData<List<Lesson>> get() = _lessons

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        fetchLessons()
    }

    private fun fetchLessons() {
        lessonRepository.fetchLessonsForCourse(courseId, onComplete = { lessonList ->
            _lessons.value = lessonList // Update LiveData with fetched lessons
        }, onError = { exception ->
            _error.value = exception.message // Handle any errors during the fetch operation
        })
    }
}
