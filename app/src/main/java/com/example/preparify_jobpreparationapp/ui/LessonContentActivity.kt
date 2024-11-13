package com.example.preparify_jobpreparationapp.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.preparify_jobpreparationapp.R
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView

class LessonContentActivity : AppCompatActivity() {
    private lateinit var lessonWebView: WebView
    private lateinit var emptyStateContainer: LinearLayout
    private lateinit var tvEmptyMessage: TextView
    private lateinit var loadingContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_content)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar_color)
        val lessonName = intent.getStringExtra("LESSON_TITLE")

        // Find the included layout as a LinearLayout
        val fixedIncludeLayout = findViewById<LinearLayout>(R.id.fixedInclude).findViewById<TextView>(R.id.titleTextView)
        fixedIncludeLayout.text =  lessonName// Set your desired text here

        fixedIncludeLayout.setOnClickListener {
            onBackPressed()
        }
        // Initialize views
        lessonWebView = findViewById(R.id.lessonWebView)
        emptyStateContainer = findViewById(R.id.emptyStateContainer)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
        loadingContainer = findViewById(R.id.loadingContainer)

        lessonWebView.settings.javaScriptEnabled = true
        lessonWebView.webViewClient = WebViewClient()

        // Initially show only loading animation
        emptyStateContainer.visibility = View.GONE
        lessonWebView.visibility = View.GONE
        loadingContainer.visibility = View.VISIBLE

        // Fetch lesson data
        fetchLessonData()
    }

    private fun fetchLessonData() {
        val lessonContent = intent.getStringExtra("LESSON_CONTENT")

        // Simulate delay for data fetching
        lessonWebView.postDelayed({
            loadingContainer.visibility = View.GONE
            if (lessonContent.isNullOrEmpty()) {
                // No content found, show empty state
                emptyStateContainer.visibility = View.VISIBLE
            } else {
                // Content available, load it in WebView
                val formattedContent = "<html><head><style>" +
                        "body { background-color: #CADCFC; color: #333; padding: 15px;}" +
                        "</style></head><body>" +
                        lessonContent +
                        "</body></html>"
                lessonWebView.loadDataWithBaseURL(null, formattedContent, "text/html", "UTF-8", null)
                lessonWebView.visibility = View.VISIBLE
            }
        }, 1000) // 2 seconds delay simulates fetching
    }

    override fun onBackPressed() {
        if (lessonWebView.canGoBack()) {
            lessonWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
