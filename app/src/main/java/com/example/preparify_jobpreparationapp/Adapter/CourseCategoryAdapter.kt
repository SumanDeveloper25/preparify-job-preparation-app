package com.example.preparify_jobpreparationapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.data.CourseCategory

class CourseCategoryAdapter(
    private var categories: List<CourseCategory>,
    private val onItemClick: (String) -> Unit // Lambda for handling clicks
) : RecyclerView.Adapter<CourseCategoryAdapter.CourseCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_category_card, parent, false)
        return CourseCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseCategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.name
        holder.categoryName.isSelected = true // Ensure marquee starts

        // Set the click listener
        holder.itemView.setOnClickListener {
            onItemClick(category.name) // Invoke the lambda with the clicked category name
        }
    }

    override fun getItemCount(): Int = categories.size

    // Method to update the categories list
    fun updateCategories(newCategories: List<CourseCategory>) {
        this.categories = newCategories
        notifyDataSetChanged()  // Notify the adapter to rebind data
    }

    class CourseCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryName: TextView = view.findViewById(R.id.categoryName)

        init {
            // Set focusable properties for marquee
            categoryName.isSelected = true
            categoryName.isFocusable = true
            categoryName.isFocusableInTouchMode = true
        }
    }
}
