package com.example.preparify_jobpreparationapp.ui.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.preparify_jobpreparationapp.Adapter.CourseAdapter
import com.example.preparify_jobpreparationapp.Adapter.CourseCategoryAdapter
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.data.Course
import com.example.preparify_jobpreparationapp.data.CourseCategory
import com.example.preparify_jobpreparationapp.ui.ViewModels.CourseCategoryViewModel
import com.example.preparify_jobpreparationapp.ui.ViewModels.CourseViewModel
import com.example.preparify_jobpreparationapp.Repository.UserRepository
import com.example.preparify_jobpreparationapp.Repository.CourseRepository
import com.example.preparify_jobpreparationapp.ui.ViewModels.CourseCategoryViewModelFactory
import com.example.preparify_jobpreparationapp.ui.ViewModels.CourseViewModelFactory

class CourseCategoryFragment : Fragment() {

    private lateinit var courseViewModel: CourseViewModel
    private lateinit var courseCategoryViewModel: CourseCategoryViewModel
    private lateinit var courseAdapter: CourseAdapter
    private var courseCategoryAdapter: CourseCategoryAdapter? = null
    private lateinit var showMoreCourseButton: LinearLayout
    private lateinit var showMoreCategoryButton: LinearLayout

    private var allCourses: List<Course> = listOf()
    private var allCategories: List<CourseCategory> = listOf()

    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course_category, container, false)
        val btnDoLater: LinearLayout = view.findViewById(R.id.btnDoLater)

        btnDoLater.setOnClickListener {
            findNavController().navigate(R.id.action_courseCategoryFragment_to_homeActivity)
        }
        // Initialize repositories and ViewModels
        userRepository = UserRepository()
        val courseRepository = CourseRepository()
        val courseFactory = CourseViewModelFactory(courseRepository)
        courseViewModel = ViewModelProvider(this, courseFactory)[CourseViewModel::class.java]

        val categoryFactory = CourseCategoryViewModelFactory(courseRepository)
        courseCategoryViewModel = ViewModelProvider(this, categoryFactory)[CourseCategoryViewModel::class.java]

        // Initialize RecyclerViews
        val courseRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewTopVCourses)
        courseRecyclerView.layoutManager = GridLayoutManager(context, 2)

        val categoryRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewTop3CourseCategory)
        categoryRecyclerView.layoutManager = GridLayoutManager(context, 2)

        showMoreCourseButton = view.findViewById(R.id.showMoreCourses)
        showMoreCategoryButton = view.findViewById(R.id.showMoreCategories)

        // Observe courses
        courseViewModel.courses.observe(viewLifecycleOwner) { courses ->
            if (courses != null) {
                allCourses = courses
                val top6Courses = courses.take(6)

                // Initialize the CourseAdapter with click listener
                courseAdapter = CourseAdapter(top6Courses) { courseName ->
                    val action = CourseCategoryFragmentDirections
                        .actionCourseCategoryFragmentToProccedCourseFragment(courseName)
                    findNavController().navigate(action)
                }
                courseRecyclerView.adapter = courseAdapter

                showMoreCourseButton.visibility = if (courses.size > 6) View.VISIBLE else View.GONE
            } else {
                Toast.makeText(requireContext(), "Failed to load courses", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe categories and set click listener for category navigation
        courseCategoryViewModel.topCourseCategories.observe(viewLifecycleOwner) { categories ->
            if (categories.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No categories available", Toast.LENGTH_SHORT).show()
            } else {
                allCategories = categories
                val top3Categories = categories.take(3) // Show top 3 categories

                courseCategoryAdapter = CourseCategoryAdapter(top3Categories) { categoryName ->
                    val action = CourseCategoryFragmentDirections
                        .actionCourseCategoryFragmentToCourseSelectionFragment(categoryName)
                    findNavController().navigate(action)
                }
                categoryRecyclerView.adapter = courseCategoryAdapter
                showMoreCategoryButton.visibility = if (categories.size > 3) View.VISIBLE else View.GONE
            }
        }

        // Show all courses when the button is clicked
        showMoreCourseButton.setOnClickListener {
            if (isUserAuthenticated()) {
                showAllCourses()
            } else {
                showAuthenticationError()
            }
        }

        // Show all categories when the button is clicked
        showMoreCategoryButton.setOnClickListener {
            if (isUserAuthenticated()) {
                showAllCategories()
            } else {
                showAuthenticationError()
            }
        }

        // Fetch data
        courseViewModel.fetchTopVCourses()
        courseCategoryViewModel.fetchTopCourseCategories()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Override the back button to close the app
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity() // Close the app
            }
        })
    }

    private fun showAllCourses() {
        courseAdapter.updateCourses(allCourses)
        showMoreCourseButton.visibility = View.GONE
    }

    private fun showAllCategories() {
        courseCategoryAdapter?.let { adapter ->
            adapter.updateCategories(allCategories)
            adapter.notifyDataSetChanged()
            showMoreCategoryButton.visibility = View.GONE
        } ?: run {
            Toast.makeText(requireContext(), "No categories available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isUserAuthenticated(): Boolean {
        return userRepository.isUserAuthenticated()
    }

    private fun showAuthenticationError() {
        Toast.makeText(requireContext(), "Please log in to view more content.", Toast.LENGTH_SHORT).show()
    }

    private fun isCourseSelect(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onCourseSelect", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}


