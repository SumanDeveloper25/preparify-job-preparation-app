package com.example.preparify_jobpreparationapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.Repository.LessonRepository

class LessonContentActivity : AppCompatActivity() {

    private lateinit var lessonWebView: WebView
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var tvEmptyMessage: TextView
    private lateinit var loadingContainer: LinearLayout
    private lateinit var btnAssessment: Button

    // Use the ViewModel
    private val lessonViewModel: LessonViewModel by viewModels { LessonViewModelFactory(LessonRepository()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_content)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar_color)

        // Get lesson and course information from intent
        val lessonName = intent.getStringExtra("LESSON_TITLE") ?: "Lesson"
        val lessonId = intent.getStringExtra("LESSON_ID")
        val courseId = intent.getStringExtra("COURSE_ID")  // Ensure COURSE_ID is passed along with LESSON_ID

        val fixedIncludeLayout =
            findViewById<LinearLayout>(R.id.fixedInclude).findViewById<TextView>(R.id.titleTextView)
        fixedIncludeLayout.text = lessonName
        fixedIncludeLayout.setOnClickListener { onBackPressed() }

        // Initialize views
        lessonWebView = findViewById(R.id.lessonWebView)
        emptyStateContainer = findViewById(R.id.emptyStateContainer)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
        loadingContainer = findViewById(R.id.loadingContainer)
        btnAssessment = findViewById(R.id.assessmentBtn)

        btnAssessment.setOnClickListener {
            if (!lessonId.isNullOrEmpty()) {
                Log.d("LessonContentActivity", "Starting quiz for Lesson ID: $lessonId")
                lessonViewModel.fetchQuestionsForLesson(lessonId)
            } else {
                Log.e("LessonContentActivity", "Lesson ID is null or empty.")
                tvEmptyMessage.text = "Unable to start quiz. Lesson ID is missing."
                emptyStateContainer.visibility = View.VISIBLE
            }
        }

        lessonWebView.settings.javaScriptEnabled = true
        lessonWebView.webViewClient = WebViewClient()

        emptyStateContainer.visibility = View.GONE
        lessonWebView.visibility = View.GONE
        loadingContainer.visibility = View.VISIBLE

        // Fetch the lesson content if lessonId and courseId are not null or empty
        if (!lessonId.isNullOrEmpty() && !courseId.isNullOrEmpty()) {
            lessonViewModel.fetchLessonContent(courseId, lessonId)
        } else {
            tvEmptyMessage.text = "Lesson or Course ID is missing."
            emptyStateContainer.visibility = View.VISIBLE
        }

        // Observers
        lessonViewModel.lessonContent.observe(this, Observer { content ->
            loadingContainer.visibility = View.GONE
            Log.d("LessonContentActivity", "Lesson Content: $content")  // Add logging to debug the content

            if (content.isNotEmpty()) {
                val formattedContent = """
                    <html>
                    <head>
                        <style>
                            body {
                                background-color: #CADCFC;
                                color: #333;
                                padding: 15px;
                                font-family: sans-serif;
                            }
                        </style>
                    </head>
                    <body>$content</body>
                    </html>
                """.trimIndent()
                lessonWebView.loadDataWithBaseURL(null, formattedContent, "text/html", "UTF-8", null)
                lessonWebView.visibility = View.VISIBLE
            } else {
                emptyStateContainer.visibility = View.VISIBLE
                tvEmptyMessage.text = "No content available for this lesson."
            }
        })

        lessonViewModel.errorMessage.observe(this, Observer { errorMessage ->
            emptyStateContainer.visibility = View.VISIBLE
            tvEmptyMessage.text = errorMessage
            Log.e("LessonContentActivity", "Error: $errorMessage")  // Log the error message
        })

        lessonViewModel.questions.observe(this, Observer { questions ->
            if (questions.isNotEmpty()) {
                val intent = Intent(this, AssessmentActivity::class.java).apply {
                    putParcelableArrayListExtra("LESSONS", ArrayList(questions))
                    putExtra("LESSON_ID", lessonId)  // Make sure to pass LESSON_ID
                    putExtra("COURSE_ID", courseId)  // Pass COURSE_ID too
                }
                startActivity(intent)
            } else {
                tvEmptyMessage.text = "No questions available for this lesson."
                emptyStateContainer.visibility = View.VISIBLE
            }
        })
    }

    override fun onBackPressed() {
        if (lessonWebView.canGoBack()) {
            lessonWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
