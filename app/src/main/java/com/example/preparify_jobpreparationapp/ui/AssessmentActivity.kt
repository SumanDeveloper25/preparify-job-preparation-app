package com.example.preparify_jobpreparationapp.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.example.preparify_jobpreparationapp.R
import com.example.preparify_jobpreparationapp.data.Lesson
import com.example.preparify_jobpreparationapp.data.Question
import com.example.preparify_jobpreparationapp.ui.ViewModels.QuizViewModel
import com.example.preparify_jobpreparationapp.ui.ViewModels.QuizViewModelFactory

class AssessmentActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var optionsLinearLayout: LinearLayout
    private lateinit var timerTextView: TextView
    private lateinit var nextButton: Button
    private lateinit var skipButton: LinearLayout
    private lateinit var questionIndexTextView: TextView

    private lateinit var questions: List<Question>
    private lateinit var lessonId: String
    private lateinit var courseId: String

    // Initialize ViewModel using the factory
    private val quizViewModel: QuizViewModel by viewModels {
        QuizViewModelFactory() // Pass necessary repository if required
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment)
        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusBar_color)

        // Initialize UI elements
        questionTextView = findViewById(R.id.questionTextView)
        optionsLinearLayout = findViewById(R.id.optionsLinearLayout)
        timerTextView = findViewById(R.id.timerTextView)
        nextButton = findViewById(R.id.nextButton)
        skipButton = findViewById(R.id.skipButton)
        questionIndexTextView = findViewById(R.id.questionIndexTextView)

        // Get Intent data
        questions = intent.getParcelableArrayListExtra("LESSONS") ?: emptyList()
        lessonId = intent.getStringExtra("LESSON_ID") ?: ""
        courseId = intent.getStringExtra("COURSE_ID") ?: ""

        // Initialize ViewModel with quiz timer
        quizViewModel.initializeQuiz(questions)

        // Observe LiveData for current question index and quiz completion state
        quizViewModel.currentQuestionIndex.observe(this) { index ->
            if (index < questions.size) {
                displayQuestion(index)
            }
        }

        quizViewModel.quizCompleted.observe(this) { completed ->
            if (completed) {
                Log.d("Assessment Activity", "Quiz Completed")
                endQuiz() // If quiz is completed, trigger the endQuiz function
            }
        }

        // Observe time left in the quiz and update the timerTextView
        quizViewModel.timeLeftInMillis.observe(this) { timeLeft ->
            val seconds = (timeLeft / 1000).toInt()
            val minutes = seconds / 60
            val secondsRemaining = seconds % 60
            timerTextView.text = String.format("%02d:%02d", minutes, secondsRemaining)
        }

        // Handle next button click
        nextButton.setOnClickListener {
            val currentIndex = quizViewModel.currentQuestionIndex.value ?: 0
            if (currentIndex < questions.size - 1) {
                quizViewModel.goToNextQuestion(questions.size) // Move to the next question
                nextButton.visibility = View.GONE // Hide next button after moving to the next question
            } else {
                // If it's the last question, mark quiz as completed
                quizViewModel._quizCompleted.value = true
            }
        }

        // Handle skip button click
        skipButton.setOnClickListener {
            quizViewModel.goToNextQuestion(questions.size)
            nextButton.visibility = View.GONE // Hide next button after skipping
        }
    }

    // Display question and options
    private fun displayQuestion(index: Int) {
        val question = questions[index]
        questionTextView.text = question.question
        questionIndexTextView.text = "Question ${index + 1} out of ${questions.size}"

        // Clear previous options and add new ones for the current question
        optionsLinearLayout.removeAllViews()

        // Create and display buttons for options
        question.options.forEach { option ->
            val button = Button(this).apply {
                text = option
                isAllCaps = false
                setOnClickListener {
                    handleAnswerSelection(this, option, question)
                }

                // Set button background, padding, and text style
                setBackgroundResource(R.drawable.quiz_option_btn)
                setPadding(16, 16, 16, 16)
                setTextColor(ContextCompat.getColor(this@AssessmentActivity, R.color.text_black))
                setTypeface(typeface, Typeface.BOLD)

                // Layout parameters for buttons
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 16)
                }
                layoutParams = params
            }

            // Add button to the layout
            optionsLinearLayout.addView(button)
        }

        // Initially, hide next button until an answer is selected
        nextButton.visibility = View.GONE
    }

    // Handle the selection of an answer
    private fun handleAnswerSelection(button: Button, selectedOption: String, question: Question) {
        // Check if the selected option is correct
        val isCorrect = question.correctAnswers.contains(selectedOption)

        // increase score if select option is correct
        quizViewModel.handleAnswerSelection(isCorrect)

        // Disable all options after selection
        optionsLinearLayout.forEach { it.isEnabled = false }

        // Loop through each option and set the background color
        optionsLinearLayout.forEach { view ->
            val optionButton = view as Button
            val optionText = optionButton.text.toString()

            // If the option is correct, highlight it with green
            if (question.correctAnswers.contains(optionText)) {
                optionButton.setBackgroundResource(R.drawable.quiz_option_btn_correct)
            } else {
                // If the option is incorrect, highlight it with red
                optionButton.setBackgroundResource(R.drawable.quiz_option_btn_incorrect)
            }

            // If the option is the selected one, high
            // light it with a distinct color
            if (optionText == selectedOption) {
                optionButton.setBackgroundResource(
                    if (isCorrect) R.drawable.quiz_option_btn_correct
                    else R.drawable.quiz_user_select_button
                )
            }
        }
        // Show the "Next" button after selection
        nextButton.visibility = View.VISIBLE
    }

    private fun endQuiz() {
        if (quizViewModel.quizCompleted.value == true) {
            val score = quizViewModel.score.value ?: 0
            quizViewModel.saveQuizResult(courseId, lessonId, questions.size)
            val intent = Intent(this@AssessmentActivity, QuizResultActivity::class.java)
            intent.putExtra("SCORE", score)
            intent.putExtra("TOTAL_QUESTION", questions.size)
            intent.putExtra("COURSE_ID", courseId)
            intent.putExtra("LESSON_ID", lessonId)
            startActivity(intent)
        }
    }

    // Override onBackPressed to show a confirmation dialog when the user tries to go back during the quiz
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit the quiz? Your progress will not be saved.")
            .setPositiveButton("Yes") { _, _ ->
                finish() // Finish the activity if the user confirms
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog if the user cancels
            }
            .setCancelable(true) // Prevent the dialog from being dismissed by tapping outside
            .show()
    }
}
