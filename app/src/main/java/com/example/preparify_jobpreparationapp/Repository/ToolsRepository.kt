package com.example.preparify_jobpreparationapp.Repository

import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.TextView
import com.example.preparify_jobpreparationapp.R

class ToolsRepository(view: View) {
    fun displaySubtitle(view: View, categoryName: String) {
        val includedView = view.findViewById<View>(R.id.fixedInclude)
        val titleTextView: TextView = includedView.findViewById(R.id.titleTextView)
        titleTextView.text = categoryName
    }
    // Fade-in animation for a smoother experience
     fun fadeInAnimation(view: View) {
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 500 // Duration for the fade-in animation (500ms)
            fillAfter = true
        }
        view.startAnimation(fadeIn)
        view.visibility = View.VISIBLE
    }
    fun getShortFormTitle(title: String): String {
        val words = title.split(" ")
        return if (words.size > 2) {
            // Convert title to initials if more than two words
            words.joinToString("") { it.first().uppercase() }
        } else {
            // Return the original title if two or fewer words
            title
        }
    }
}