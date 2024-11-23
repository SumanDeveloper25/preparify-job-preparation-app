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
import com.example.preparify_jobpreparationapp.Repository.LessonRepository

class LessonActivity : AppCompatActivity() {
    private lateinit var courseTitleTextView: TextView
    private lateinit var courseId: String
    private lateinit var courseTitle: String
    private lateinit var lessonRecyclerView: RecyclerView
    private lateinit var lessonAdapter: LessonAdapter // Declare adapter without initialization here

    // ViewModel initialization with LessonRepository provided to the factory
    private val lessonRepository = LessonRepository() // Initialize the repository
    private val viewModel: LessonViewModel by viewModels {
        LessonViewModelFactory(lessonRepository) // Pass the repository to the factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar_color)

        // Get the data passed from the previous activity
        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        courseTitle = intent.getStringExtra("COURSE_TITLE") ?: ""

        // Find the included layout and set the course title
        val fixedIncludeLayout = findViewById<LinearLayout>(R.id.fixedInclude)
        val subtitleTextView = fixedIncludeLayout.findViewById<TextView>(R.id.titleTextView)
        subtitleTextView.text = courseTitle // Set your desired text here

        // Set up RecyclerView with courseId passed to the adapter
        lessonAdapter = LessonAdapter(listOf(), courseId) // Pass courseId here
        lessonRecyclerView = findViewById(R.id.recyclerViewLessons)
        lessonRecyclerView.layoutManager = LinearLayoutManager(this)
        lessonRecyclerView.adapter = lessonAdapter

        // Observe lessons LiveData and update the adapter
        viewModel.lessons.observe(this) { lessons ->
            lessonAdapter.updateLessons(lessons) // Update the adapter with fetched lessons
        }

        // Observe error messages (if any)
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                // Handle error (e.g., show a Toast or Snackbar)
            }
        }

        // Fetch lessons for the given courseId
        if (courseId.isNotEmpty()) {
            viewModel.fetchLessons(courseId) // Pass the courseId to fetch lessons
        }
    }
}
