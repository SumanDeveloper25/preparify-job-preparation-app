package com.example.preparify_jobpreparationapp.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.preparify_jobpreparationapp.Adapter.CourseAdapter
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.Repository.CourseRepository
import com.example.preparify_jobpreparationapp.Repository.ToolsRepository
import com.example.preparify_jobpreparationapp.ui.HomeActivity
import com.example.preparify_jobpreparationapp.ui.ViewModels.CourseViewModel
import com.example.preparify_jobpreparationapp.ui.ViewModels.CourseViewModelFactory

class CourseSelectionFragment : Fragment() {

    private lateinit var rvLatestCourses: RecyclerView
    private lateinit var rvTrendingCourses: RecyclerView
    private lateinit var latestCourseAdapter: CourseAdapter
    private lateinit var trendingCourseAdapter: CourseAdapter
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var tools: ToolsRepository
    private val args: CourseSelectionFragmentArgs by navArgs()

    private lateinit var noCoursesImage: ImageView
    private lateinit var popularCoursesText: TextView
    private lateinit var latestCoursesText: TextView
    private lateinit var noCoursetext: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course_selection, container, false)
        // Get the category name from arguments
        val categoryName = args.categoryName

        tools = ToolsRepository(view)
        tools.displaySubtitle(view, categoryName)
        val fixedIncludeLayout = view.findViewById<LinearLayout>(R.id.fixedInclude)

        fixedIncludeLayout.setOnClickListener {
            startActivity(Intent(inflater.context, HomeActivity::class.java))
        }


        // Initialize RecyclerViews
        rvLatestCourses = view.findViewById(R.id.rvLatestCourses)
        rvTrendingCourses = view.findViewById(R.id.rvTrendingCourses)

        rvLatestCourses.layoutManager = GridLayoutManager(requireContext(), 2)
        rvTrendingCourses.layoutManager = GridLayoutManager(requireContext(), 2)

        // Initialize adapters with click listeners
        latestCourseAdapter = CourseAdapter(listOf()) { courseName ->
            val action = CourseSelectionFragmentDirections
                .actionCourseSelectionFragmentToProccedCourseFragment(courseName)
            findNavController().navigate(action)
        }

        trendingCourseAdapter = CourseAdapter(listOf()) { courseName ->
            val action = CourseSelectionFragmentDirections
                .actionCourseSelectionFragmentToProccedCourseFragment(courseName)
            findNavController().navigate(action)
        }

        rvLatestCourses.adapter = latestCourseAdapter
        rvTrendingCourses.adapter = trendingCourseAdapter

        // Initialize ImageView and TextViews for "no courses" message
        noCoursesImage = view.findViewById(R.id.noCoursesImage)
        popularCoursesText = view.findViewById(R.id.tvPopularCourses)
        latestCoursesText = view.findViewById(R.id.tvLatestCourses)
        noCoursetext = view.findViewById(R.id.tvNoCourseAvailable)

        // Set up CourseRepository and CourseViewModel with factory
        val courseRepository = CourseRepository()
        val factory = CourseViewModelFactory(courseRepository)
        courseViewModel = ViewModelProvider(this, factory)[CourseViewModel::class.java]

        // Observe and set courses
        courseViewModel.latestCourses.observe(viewLifecycleOwner) { latestCourses ->
            courseViewModel.trendingCourses.observe(viewLifecycleOwner) { trendingCourses ->
                if (latestCourses.isEmpty() && trendingCourses.isEmpty()) {
                    noCoursesImage.visibility = View.VISIBLE
                    rvLatestCourses.visibility = View.GONE
                    rvTrendingCourses.visibility = View.GONE
                    popularCoursesText.visibility = View.GONE
                    latestCoursesText.visibility = View.GONE
                    noCoursetext.visibility = View.VISIBLE
                } else {
                    popularCoursesText.visibility = if (latestCourses.size == 1) View.GONE else View.VISIBLE
                    noCoursesImage.visibility = View.GONE
                    rvLatestCourses.visibility = View.VISIBLE
                    rvTrendingCourses.visibility = View.VISIBLE
                    latestCoursesText.visibility = View.VISIBLE
                    noCoursetext.visibility = View.GONE
                    latestCourseAdapter.setCourses(latestCourses)
                    trendingCourseAdapter.setCourses(trendingCourses)
                }
            }
        }

        // Fetch courses by category
        courseViewModel.fetchCoursesByCategory(categoryName)

        return view
    }
}
