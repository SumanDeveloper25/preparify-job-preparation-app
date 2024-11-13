package com.example.preparify_jobpreparationapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.data.Course
import com.squareup.picasso.Picasso

class CourseAdapter(
    private var courseList: List<Course>,
    private val onCourseClick: (String) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    // ViewHolder class to hold the view items
    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.courseTitle)
        private val image: ImageView = itemView.findViewById(R.id.courseImage)

        // Bind data to the ViewHolder
        fun bind(course: Course, onCourseClick: (String) -> Unit) {
            // If title is longer than two words, we get the initials or short form
            title.text = getShortFormTitle(course.title)

            // Load course image using Picasso
            if (course.logoUrl.isNotEmpty()) {
                Picasso.get()
                    .load(course.logoUrl)
                    .error(R.drawable.ic_error) // Handle image load error
                    .into(image)
            } else {
                image.setImageResource(R.drawable.not_available) // Placeholder image if no logoUrl
            }

            // Set click listener on the item view
            itemView.setOnClickListener {
                onCourseClick(course.title) // Call the click listener with course title
            }
        }

        // Function to get short form title if it contains more than two words
        private fun getShortFormTitle(title: String): String {
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

    // Method to update the list of courses dynamically
    fun setCourses(newCourses: List<Course>) {
        courseList = newCourses
        notifyDataSetChanged() // Notify the adapter of the data change to refresh the view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_card, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]
        holder.bind(course, onCourseClick)
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    // Method to update the list of courses dynamically
    fun updateCourses(newCourses: List<Course>) {
        courseList = newCourses
        notifyDataSetChanged()  // Notify the adapter of the data change to refresh the view
    }
}
