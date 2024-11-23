package com.example.preparify_jobpreparationapp.ui.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.Repository.CourseRepository
import com.example.preparify_jobpreparationapp.ui.ViewModels.ProccedCourseViewModel
import com.example.preparify_jobpreparationapp.ui.ViewModels.ProccedCourseViewModelFactory
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ProccedCourseFragment : Fragment() {

    private lateinit var courseNameTextView: TextView
    private lateinit var courseImageView: ImageView
    private lateinit var courseDetailsLayout: LinearLayout
    private lateinit var loadingAnimationView: LottieAnimationView
    private lateinit var proceedButton: Button

    // ViewModel for handling course data
    private val viewModel: ProccedCourseViewModel by viewModels {
        ProccedCourseViewModelFactory(CourseRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_procced_course, container, false)

        // Initialize views
        courseNameTextView = view.findViewById(R.id.courseNameTextView)
        courseImageView = view.findViewById(R.id.courseImageView)
        courseDetailsLayout = view.findViewById(R.id.courseDetailsLayout)
        loadingAnimationView = view.findViewById(R.id.loadingAnimationView)
        proceedButton = view.findViewById(R.id.proceedButton)

        // Retrieve course ID from arguments
        val args = ProccedCourseFragmentArgs.fromBundle(requireArguments())
        val courseId = args.courseId

        // Observe ViewModel data
        observeViewModel()

        // Fetch course data
        viewModel.fetchCourseDetails(courseId)

        proceedButton.setOnClickListener {
            val action = ProccedCourseFragmentDirections
                .actionProccedCourseFragmentToSetUpCourseFragment(courseId)
            findNavController().navigate(action)
        }

        return view
    }

    // Function to observe ViewModel data
    private fun observeViewModel() {
        viewModel.courseData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                // Log the fetched data for debugging
                Log.d("ProceedCourseFragment", "Course Data: $data")
                courseNameTextView.text = data["title"] as? String ?: "No Course Name"
                val logoUrl = data["logoUrl"] as? String
                loadCourseLogo(logoUrl)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingAnimationView.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                courseNameTextView.text = error
            }
        }
    }

    // Load course logo using Picasso and handle animations
    private fun loadCourseLogo(logoUrl: String?) {
        if (!logoUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(logoUrl)
                .placeholder(R.drawable.not_available)
                .error(R.drawable.not_available)
                .into(courseImageView, object : Callback {
                    override fun onSuccess() {
                        courseDetailsLayout.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        courseImageView.setImageResource(R.drawable.not_available)
                    }
                })
        } else {
            courseImageView.setImageResource(R.drawable.not_available)
        }
    }
}
