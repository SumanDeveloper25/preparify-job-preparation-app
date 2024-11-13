package com.example.preparify_jobpreparationapp.ui.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.data.Lesson
import com.example.preparify_jobpreparationapp.ui.LessonContentActivity
import com.airbnb.lottie.LottieAnimationView

class LessonAdapter(private var lessons: List<Lesson>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LESSON = 1
    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_LOADING = 2

    private var isLoading = true // Flag to indicate loading state

    // ViewHolder for lesson items
    class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lessonNumber: TextView = itemView.findViewById(R.id.tvLessonNumber)
        val lessonTitle: TextView = itemView.findViewById(R.id.tvLessonTitle)
        val lessonDescription: TextView = itemView.findViewById(R.id.tvLessonDescription)
    }

    // ViewHolder for empty state
    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emptyMessage: TextView = itemView.findViewById(R.id.tvEmptyMessage)
        val animationView: LottieAnimationView = itemView.findViewById(R.id.lottieAnimationView)
    }

    // ViewHolder for loading state
    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadingAnimation: LottieAnimationView = itemView.findViewById(R.id.lottieLoadingAnimation)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoading -> VIEW_TYPE_LOADING
            lessons.isEmpty() -> VIEW_TYPE_EMPTY
            else -> VIEW_TYPE_LESSON
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LESSON -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lesson, parent, false)
                LessonViewHolder(view)
            }
            VIEW_TYPE_EMPTY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_empty, parent, false)
                EmptyViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LessonViewHolder -> {
                val lesson = lessons[position]
                holder.lessonNumber.text = String.format("%02d", position + 1)
                holder.lessonTitle.text = lesson.title
                holder.lessonDescription.text = lesson.subtitle

                holder.itemView.setOnClickListener {
                    val context = holder.itemView.context
                    val intent = Intent(context, LessonContentActivity::class.java).apply {
                        putExtra("LESSON_ID", lesson.lessonId)
                        putExtra("LESSON_TITLE", lesson.title)
                        putExtra("LESSON_CONTENT", lesson.content)
                    }
                    context.startActivity(intent)
                }
            }
            is EmptyViewHolder -> {
                holder.emptyMessage.text = "Currently, no lessons have been added."
                holder.animationView.playAnimation()
            }
            is LoadingViewHolder -> {
                holder.loadingAnimation.playAnimation()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading || lessons.isEmpty()) 1 else lessons.size
    }

    fun setLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }

    fun updateLessons(newLessons: List<Lesson>) {
        lessons = newLessons
        setLoading(false) // Stop loading once data is set
    }
}
