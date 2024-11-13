package com.example.preparify_jobpreparationapp.ui.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.data.Course
import com.example.preparify_jobpreparationapp.ui.LessonActivity
import com.squareup.picasso.Picasso

class EnrolledCoursesAdapter(private var courses: List<Course>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var showAll = false
    private var isLoading = true

    companion object {
        private const val TYPE_LOADING = 0
        private const val TYPE_COURSE = 1
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.tvCourseName)
        val courseCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val courseLogo: ImageView = itemView.findViewById(R.id.courseLogo)
        val courseProgress: ProgressBar = itemView.findViewById(R.id.courseProgress)
        val courseProgressPercentage: TextView = itemView.findViewById(R.id.courseProgressPercentage)
    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadingAnimation: ImageView = itemView.findViewById(R.id.loadingAnimation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_LOADING) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loading_skeleton_course, parent, false)
            LoadingViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.home_card_course, parent, false)
            CourseViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CourseViewHolder) {
            val course = courses[position]
            holder.courseName.text = getShortFormTitle(course.title)
            holder.courseCategory.text = course.category

            val progress = calculateProgress(course)
            holder.courseProgress.progress = progress
            holder.courseProgressPercentage.text = "$progress%"

            if (course.logoUrl.isNotEmpty()) {
                Picasso.get()
                    .load(course.logoUrl)
                    .error(R.drawable.ic_error)
                    .into(holder.courseLogo)
            } else {
                holder.courseLogo.setImageResource(R.drawable.not_available)
            }

            holder.itemView.setOnClickListener {
                val context: Context = holder.itemView.context
                val intent = Intent(context, LessonActivity::class.java).apply {
                    putExtra("COURSE_ID", course.title)
                    putExtra("COURSE_TITLE", course.title)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) TYPE_LOADING else TYPE_COURSE
    }

    override fun getItemCount(): Int {
        return if (isLoading) 2 else (if (showAll) courses.size else minOf(courses.size, 2))
    }

    fun setLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }

    fun setShowAll(showAll: Boolean) {
        this.showAll = showAll
        notifyDataSetChanged()
    }

    fun updateCourses(newCourses: List<Course>) {
        this.courses = newCourses
        notifyDataSetChanged()
    }

    private fun calculateProgress(course: Course): Int {
        val totalLessons = course.lessons.size
        val completedLessons = course.lessons.count { it.isCompleted }

        return if (totalLessons > 0) {
            (completedLessons * 100) / totalLessons
        } else {
            0
        }
    }

    private fun getShortFormTitle(title: String): String {
        val words = title.split(" ")
        return if (words.size > 2) {
            words.joinToString("") { it.first().uppercase() }
        } else {
            title
        }
    }
}
