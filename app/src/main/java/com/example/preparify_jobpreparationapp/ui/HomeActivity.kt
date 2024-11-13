package com.example.preparify_jobpreparationapp.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.ui.Fragments.AssessmentFragment
import com.example.preparify_jobpreparationapp.ui.Fragments.PremiumFragment
import com.example.preparify_jobpreparationapp.ui.Fragments.SettingsFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var linearLayoutDashboard: LinearLayout
    private lateinit var linearLayoutAssessment: LinearLayout
    private lateinit var linearLayoutPremium: LinearLayout
    private lateinit var linearLayoutSettings: LinearLayout

    // Declare TextView and ImageView for each layout to change their color
    private lateinit var dashboardIcon: ImageView
    private lateinit var dashboardText: TextView
    private lateinit var assessmentIcon: ImageView
    private lateinit var assessmentText: TextView
    private lateinit var premiumIcon: ImageView
    private lateinit var premiumText: TextView
    private lateinit var settingsIcon: ImageView
    private lateinit var settingsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar_color)

        // Initialize layouts and views for icons and text
        linearLayoutDashboard = findViewById(R.id.linearLayoutDashboard)
        dashboardIcon = linearLayoutDashboard.findViewById(R.id.dashboardIcon)
        dashboardText = linearLayoutDashboard.findViewById(R.id.dashboardText)

        linearLayoutAssessment = findViewById(R.id.linearLayoutAssessment)
        assessmentIcon = linearLayoutAssessment.findViewById(R.id.assessmentIcon)
        assessmentText = linearLayoutAssessment.findViewById(R.id.assessmentText)

        linearLayoutPremium = findViewById(R.id.linearLayoutPremium)
        premiumIcon = linearLayoutPremium.findViewById(R.id.premiumIcon)
        premiumText = linearLayoutPremium.findViewById(R.id.premiumText)

        linearLayoutSettings = findViewById(R.id.linearLayoutSettings)
        settingsIcon = linearLayoutSettings.findViewById(R.id.settingsIcon)
        settingsText = linearLayoutSettings.findViewById(R.id.settingsText)

        // Load DashboardFragment by default
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
            updateSelectedLayout(linearLayoutDashboard)
        }

        // Set up click listeners for each LinearLayout to switch between fragments
        linearLayoutDashboard.setOnClickListener {
            loadFragment(DashboardFragment())
            updateSelectedLayout(linearLayoutDashboard)
        }

        linearLayoutAssessment.setOnClickListener {
            loadFragment(AssessmentFragment())
            updateSelectedLayout(linearLayoutAssessment)
        }

        linearLayoutPremium.setOnClickListener {
            loadFragment(PremiumFragment())
            updateSelectedLayout(linearLayoutPremium)
        }

        linearLayoutSettings.setOnClickListener {
            loadFragment(SettingsFragment())
            updateSelectedLayout(linearLayoutSettings)
        }
    }

    // Helper function to replace fragment only if it's a different fragment
    private fun loadFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (currentFragment != null && currentFragment::class == fragment::class) return

        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Helper function to update icon and text color for the selected layout
    private fun updateSelectedLayout(selectedLayout: LinearLayout) {
        val selectedColor = ContextCompat.getColor(this, R.color.main_color)
        val defaultColor = ContextCompat.getColor(this, R.color.text_black)

        // Reset all icons and text to default color
        dashboardIcon.setColorFilter(defaultColor)
        dashboardText.setTextColor(defaultColor)
        assessmentIcon.setColorFilter(defaultColor)
        assessmentText.setTextColor(defaultColor)
        premiumIcon.setColorFilter(defaultColor)
        premiumText.setTextColor(defaultColor)
        settingsIcon.setColorFilter(defaultColor)
        settingsText.setTextColor(defaultColor)

        // Apply selected color to the appropriate icon and text
        when (selectedLayout.id) {
            R.id.linearLayoutDashboard -> {
                dashboardIcon.setColorFilter(selectedColor)
                dashboardText.setTextColor(selectedColor)
            }
            R.id.linearLayoutAssessment -> {
                assessmentIcon.setColorFilter(selectedColor)
                assessmentText.setTextColor(selectedColor)
            }
            R.id.linearLayoutPremium -> {
                premiumIcon.setColorFilter(selectedColor)
                premiumText.setTextColor(selectedColor)
            }
            R.id.linearLayoutSettings -> {
                settingsIcon.setColorFilter(selectedColor)
                settingsText.setTextColor(selectedColor)
            }
        }
    }
}
