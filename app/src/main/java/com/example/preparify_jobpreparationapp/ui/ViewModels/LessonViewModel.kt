package com.example.preparify_jobpreparationapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.preparify_jobpreparationapp.data.Lesson
import com.example.preparify_jobpreparationapp.data.Question
import com.example.preparify_jobpreparationapp.Repository.LessonRepository

class LessonViewModel(private val lessonRepository: LessonRepository) : ViewModel() {

    private val _lessons = MutableLiveData<List<Lesson>>()
    val lessons: LiveData<List<Lesson>> get() = _lessons

    private val _lessonContent = MutableLiveData<String>()
    val lessonContent: LiveData<String> get() = _lessonContent

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> get() = _questions

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Fetch questions for a lesson
    fun fetchQuestionsForLesson(lessonId: String) {
        lessonRepository.fetchQuestionsForLesson(lessonId, { questions ->
            _questions.value = questions
        }, { error ->
            _errorMessage.value = "Error loading questions: ${error.message}"
            Log.e("LessonViewModel", "Error loading questions", error)
        })
    }

    // Fetch lessons for a course
    fun fetchLessons(courseId: String) {
        lessonRepository.fetchLessonsForCourse(courseId, { lessons ->
            _lessons.value = lessons // Update the list of lessons
        }, { exception ->
            _errorMessage.value = "Error loading lessons: ${exception.message}"
            Log.e("LessonViewModel", "Error fetching lessons", exception)
        })
    }

    // Fetch lesson content with both courseId and lessonId
    fun fetchLessonContent(courseId: String, lessonId: String) {
        lessonRepository.fetchLessonContent(courseId, lessonId, { content ->
            _lessonContent.value = content // Set the content to LiveData
        }, { error ->
            _errorMessage.value = "Error loading lesson content: ${error.message}"
            Log.e("LessonViewModel", "Error loading lesson content", error)
        })
    }
}
