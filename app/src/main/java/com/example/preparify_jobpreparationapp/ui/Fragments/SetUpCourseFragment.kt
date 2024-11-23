package com.example.preparify_jobpreparationapp.ui.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.Repository.EnrolledCourseRepository
import com.example.preparify_jobpreparationapp.ui.HomeActivity
import com.example.preparify_jobpreparationapp.ui.ViewModels.EnrolledCourseViewModel
import com.example.preparify_jobpreparationapp.ui.ViewModels.EnrolledCourseViewModelFactory
import com.example.preparify_jobpreparationapp.data.Course

class SetUpCourseFragment : Fragment() {

    private lateinit var courseId: String

    private val viewModel: EnrolledCourseViewModel by viewModels {
        EnrolledCourseViewModelFactory(EnrolledCourseRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_set_up_course, container, false)

        // Retrieve the courseId from the arguments
        val args = SetUpCourseFragmentArgs.fromBundle(requireArguments())
        courseId = args.courseId

        // Fetch course data and enroll in the course
        fetchCourseDataAndEnroll(courseId)

        return view
    }

    private fun fetchCourseDataAndEnroll(courseId: String) {
        // Fetch course data using ViewModel with success and failure callbacks
        viewModel.fetchCourseData(courseId, { course -> // onSuccess callback
            // Once course data is successfully fetched, enroll in the course using the course object
            viewModel.enrollInCourse(course) // Pass the Course object for enrollment
        }, { errorMessage -> // onFailure callback
            // Show error if course data fetch fails
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        })

        // Observe ViewModel for enrollment status updates
        viewModel.enrollmentStatus.observe(viewLifecycleOwner, Observer { status ->
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()

            // If enrollment is successful, navigate to HomeActivity after 3 seconds
            if (status == "Successfully enrolled in the course") {
                isCourseSelect()
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_setUpCourseFragment_to_homeActivity)

                }, 3000) // 3000 milliseconds = 3 seconds
            } else {
                findNavController().navigate(R.id.action_setUpCourseFragment_to_homeActivity)
            }
        })
    }

    private fun navigateToHomeActivity() {
        // Create an intent to start HomeActivity
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Optional: close the current activity if needed
    }

    private fun isCourseSelect() {
        val sharedPref = requireActivity().getSharedPreferences("onCourseSelect", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}
