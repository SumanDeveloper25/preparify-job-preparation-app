package com.example.preparify_jobpreparationapp.ui.OnBoarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.preparify_jobpreparationapp.R

class SecondScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second_screen, container, false)

        // Get ViewPager2 from the parent activity
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        // Reference btnNext1 using findViewById
        val btnNext = view.findViewById<Button>(R.id.btnNext2)

        // Set the click listener for btnNext1 to navigate to the next page
        btnNext.setOnClickListener {
            viewPager?.currentItem = 2
        }

        view.findViewById<LinearLayout>(R.id.btnSkip).setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_loginOptionsFragment)
        }

        return view
    }
}