package com.example.preparify_jobpreparationapp.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.ui.Adapters.LessonAdapter
import com.example.preparify_jobpreparationapp.ui.ViewModels.LessonViewModel
import com.example.preparify_jobpreparationapp.ui.ViewModels.LessonViewModelFactory

class LessonActivity : AppCompatActivity() {
    private lateinit var courseTitleTextView: TextView
    private lateinit var courseId: String
    private lateinit var courseTitle: String
    private lateinit var lessonRecyclerView: RecyclerView
    private val lessonAdapter = LessonAdapter(listOf())

    private val viewModel: LessonViewModel by viewModels {
        LessonViewModelFactory(courseId) // Pass courseId to the factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar_color)

        // Get the data passed from the previous activity
        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        courseTitle = intent.getStringExtra("COURSE_TITLE") ?: ""

        // Find the included layout as a LinearLayout
        val fixedIncludeLayout = findViewById<LinearLayout>(R.id.fixedInclude)
        val subtitleTextView = fixedIncludeLayout.findViewById<TextView>(R.id.titleTextView)
        subtitleTextView.text = courseTitle // Set your desired text here


        // Set up RecyclerView
        lessonRecyclerView = findViewById(R.id.recyclerViewLessons)
        lessonRecyclerView.layoutManager = LinearLayoutManager(this)
        lessonRecyclerView.adapter = lessonAdapter

        // Observe the lessons LiveData
        viewModel.lessons.observe(this) { lessons ->
            lessonAdapter.updateLessons(lessons) // Update the adapter with fetched lessons
        }

        // Observe error messages (if any)
        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                // Display error message to user
            }
        }
    }
}
