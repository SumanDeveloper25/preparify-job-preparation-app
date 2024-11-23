package com.example.preparify_jobpreparationapp.ui.ViewModels

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.preparify_jobpreparationapp.Repository.UserRepository
import com.example.preparify_jobpreparationapp.data.Lesson
import com.example.preparify_jobpreparationapp.data.Question
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Timer
import kotlin.concurrent.schedule

class QuizViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _timeLeftInMillis = MutableLiveData<Long>()
    val timeLeftInMillis: LiveData<Long> = _timeLeftInMillis

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    val _quizCompleted = MutableLiveData(false)
    val quizCompleted: LiveData<Boolean> = _quizCompleted

    private val _lesson = MutableLiveData<Lesson?>()
    val lesson: LiveData<Lesson?> get() = _lesson

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private var timer: Timer? = null
    private val handler = Handler()

    // Initialize the quiz with a list of questions
    fun initializeQuiz(questionsList: List<Question>) {
        Log.d("QuizViewModel", "Initializing quiz with ${questionsList.size} questions")
        _questions.value = questionsList
        val totalTime = questionsList.size * 20 * 1000L // 20 seconds per question
        _timeLeftInMillis.value = totalTime
        startTimer(totalTime)
    }

    // Start the countdown timer
    private fun startTimer(timeInMillis: Long) {
        Log.d("QuizViewModel", "Starting timer for $timeInMillis ms")
        timer?.cancel()
        timer = Timer()

        timer?.schedule(0, 1000) {
            handler.post {
                val currentTime = _timeLeftInMillis.value ?: timeInMillis
                if (currentTime > 0) {
                    _timeLeftInMillis.value = currentTime - 1000
                } else {
                    Log.d("QuizViewModel", "Time is up for the current question")
                    goToNextQuestion(_questions.value?.size ?: 0)
                }
            }
        }
    }

    // Increment the score
    private fun incrementScore() {
        _score.value = (_score.value ?: 0) + 1
        Log.d("QuizViewModel", "Score incremented to ${_score.value}")
    }

    // Handle answer selection
    fun handleAnswerSelection(isCorrect: Boolean) {
        if (isCorrect) {
            Log.d("QuizViewModel", "Correct answer selected")
            incrementScore()
        } else {
            Log.d("QuizViewModel", "Incorrect answer selected")
        }
    }

    // Move to the next question
    fun goToNextQuestion(totalQuestions: Int) {
        if (_quizCompleted.value == true) return

        if ((_currentQuestionIndex.value ?: 0) < (totalQuestions - 1)) {
            _currentQuestionIndex.value = (_currentQuestionIndex.value ?: 0) + 1
            Log.d("QuizViewModel", "Navigating to question ${_currentQuestionIndex.value}")
        } else {
            _quizCompleted.value = true
            Log.d("QuizViewModel", "Quiz completed")
        }
    }

    // Calculate and update user's progress in the course
    private fun calculateProgress(courseId: String) {
        val userId = userRepository.getAuthUserId() ?: return
        Log.d("QuizViewModel", "Calculating progress for user $userId in course $courseId")

        firestore.collection("users")
            .document(userId)
            .collection("enrolledCourses")
            .document(courseId)
            .collection("lessons")
            .get()
            .addOnSuccessListener { snapshot ->
                val lessons = snapshot.toObjects(Lesson::class.java)

                val completedLessons = lessons.count { it.isCompleted }
                val totalLessons = lessons.size
                val progress = if (totalLessons > 0) {
                    (completedLessons * 100) / totalLessons
                } else {
                    0
                }

                Log.d("QuizViewModel", "Total lessons: $totalLessons, Completed lessons: $completedLessons")
                Log.d("QuizViewModel", "Calculated progress: $progress%")

                firestore.collection("users")
                    .document(userId)
                    .collection("enrolledCourses")
                    .document(courseId)
                    .update("progress", progress)
                    .addOnSuccessListener {
                        _progress.postValue(progress)
                        Log.d("QuizViewModel", "User's progress updated to: $progress%")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("QuizViewModel", "Failed to update user's progress: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Failed to fetch user's lessons: ${exception.message}")
            }
    }

    // Save quiz result and update user
    fun saveQuizResult(courseId: String, lessonId: String, totalQuestions: Int) {
        val userId = userRepository.getAuthUserId() ?: return
        Log.d("QuizViewModel", "Saving quiz result for user $userId in lesson $lessonId of course $courseId")

        // Fetch the user's enrolled courses
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { userDocument ->
                val enrolledCourses = userDocument.get("enrolledCourses") as? List<String> ?: emptyList()

                // Check if the user is enrolled in the given course
                if (courseId in enrolledCourses) {
                    Log.d("QuizViewModel", "User is enrolled in the course $courseId")

                    // Now fetch the lesson within the enrolled course
                    firestore.collection("users")
                        .document(userId)
                        .collection("enrolledCourses")
                        .document(courseId)
                        .collection("lessons")
                        .document(lessonId)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                Log.d("QuizViewModel", "Lesson document found: ${documentSnapshot.data}")
                                val isAlreadyCompleted = documentSnapshot.getBoolean("isCompleted") ?: false

                                if (!isAlreadyCompleted) {
                                    firestore.collection("users")
                                        .document(userId)
                                        .collection("enrolledCourses")
                                        .document(courseId)
                                        .collection("lessons")
                                        .document(lessonId)
                                        .update(
                                            mapOf(
                                                "isCompleted" to true,
                                                "score" to "${_score.value ?: 0}/${totalQuestions}"
                                            )
                                        )
                                        .addOnSuccessListener {
                                            Log.d("QuizViewModel", "Lesson $lessonId marked as completed.")
                                            calculateProgress(courseId)
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("QuizViewModel", "Failed to update lesson $lessonId: ${exception.message}")
                                        }
                                } else {
                                    Log.d("QuizViewModel", "Lesson $lessonId already completed.")
                                    calculateProgress(courseId)
                                }
                            } else {
                                Log.e("QuizViewModel", "Lesson $lessonId not found at the path.")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("QuizViewModel", "Failed to fetch lesson $lessonId: ${exception.message}")
                        }
                } else {
                    Log.e("QuizViewModel", "User is not enrolled in the course $courseId.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QuizViewModel", "Failed to fetch user's enrolled courses: ${exception.message}")
            }
    }


}