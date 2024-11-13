package com.example.preparify_jobpreparationapp.ui.OnBoarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.preparify_jobpreparationapp.R

class ThirdScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        // Set the click listener for btnNext1 to navigate to the next page
        view.findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_loginOptionsFragment)
            onBoardingFinished()
        }
        view.findViewById<LinearLayout>(R.id.btnBack).setOnClickListener {
            viewPager?.currentItem = 0
        }
        return view
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}