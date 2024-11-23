package com.example.preparify_jobpreparationapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.databinding.ActivityQuizResultBinding

class QuizResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_result)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.cream)

        val score = intent.getIntExtra("SCORE", 0)
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTION", 0)
        val courseId = intent.getStringExtra("COURSE_ID") ?: "Unknown"
        val lessonId = intent.getStringExtra("LESSON_ID") ?: "Unknown"

        binding.scoreText = "Your Score is: $score/$totalQuestions/$courseId/$lessonId"

    }
}

