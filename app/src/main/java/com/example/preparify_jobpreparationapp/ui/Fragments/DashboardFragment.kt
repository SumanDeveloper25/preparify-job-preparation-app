package com.example.preparify_jobpreparationapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.Repository.EnrolledCourseRepository
import com.example.preparify_jobpreparationapp.Repository.UserRepository
import com.example.preparify_jobpreparationapp.ui.Adapters.EnrolledCoursesAdapter
import com.example.preparify_jobpreparationapp.ui.ViewModels.EnrolledCourseViewModel
import com.example.preparify_jobpreparationapp.ui.ViewModels.EnrolledCourseViewModelFactory
import java.util.Calendar

class DashboardFragment : Fragment() {

    private val viewModel: EnrolledCourseViewModel by viewModels {
        EnrolledCourseViewModelFactory(EnrolledCourseRepository())
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var enrolledCoursesAdapter: EnrolledCoursesAdapter
    private lateinit var viewAllTextView: TextView
    private lateinit var userNameTextView: TextView
    private lateinit var currentGreeting: TextView
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userNameTextView = view.findViewById(R.id.user_name)
        currentGreeting = view.findViewById(R.id.current_time)

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEnrolledCourses)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Initialize "View All" TextView
        viewAllTextView = view.findViewById(R.id.textView2)

        // Initialize adapter and set it to RecyclerView with loading state
        enrolledCoursesAdapter = EnrolledCoursesAdapter(emptyList())
        recyclerView.adapter = enrolledCoursesAdapter
        enrolledCoursesAdapter.setLoading(true)

        // Initialize UserRepository
        userRepository = UserRepository()

        // Fetch and set user name
        fetchAndSetUserName()
        currentGreeting.text = getCurrentGreeting()

        // Fetch user courses
        val userId = userRepository.getAuthUserId()
        if (userId != null) {
            viewModel.fetchUserCourses(userId)
        }

        // Observe the enrolled courses LiveData
        viewModel.enrolledCourses.observe(viewLifecycleOwner) { courses ->
            enrolledCoursesAdapter.setLoading(false)  // Turn off loading animation
            enrolledCoursesAdapter.updateCourses(courses ?: emptyList())
        }

        // Observe the error message
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                enrolledCoursesAdapter.setLoading(false)
            }
        }

        // Set "View All" functionality
        viewAllTextView.setOnClickListener {
            enrolledCoursesAdapter.setShowAll(true)
        }
    }

    private fun fetchAndSetUserName() {
        userRepository.getAuthUserName { userName ->
            val displayName = if (!userName.isNullOrEmpty()) "$userName" else "User"
            val formattedName = "Hi, "+
                displayName.split(" ")
                    .firstOrNull()?.lowercase()
                    ?.replaceFirstChar { it.uppercaseChar() }
            userNameTextView.text = formattedName
        }
    }
    private fun getCurrentGreeting(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }
}
