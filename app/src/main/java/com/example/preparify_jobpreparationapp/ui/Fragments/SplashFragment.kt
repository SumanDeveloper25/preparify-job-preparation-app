package com.example.preparify_jobpreparationapp.ui.Fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.preparify_jobpreparationapp.R

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler().postDelayed({
            if(onSetupFinished()) {
                findNavController().navigate(R.id.action_splashFragment_to_homeActivity2)
            }
            else if (onBoardingFinished()) {
                findNavController().navigate(R.id.action_splashFragment_to_loginOptionsFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        }, 3000)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
    private fun onSetupFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onSetup", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }
}