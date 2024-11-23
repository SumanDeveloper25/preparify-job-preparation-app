package com.example.preparify_jobpreparationapp.Repository

import androidx.lifecycle.MutableLiveData
import com.example.preparify_jobpreparationapp.data.Question
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class AssessmentRepository {
    private val db = FirebaseFirestore.getInstance()

    fun fetchQuestions(lessonId: String): MutableLiveData<List<Question>> {
        val questionsLiveData = MutableLiveData<List<Question>>()

        db.collection("questions")
            .whereEqualTo("belongLesson", lessonId)
            .get()
            .addOnSuccessListener { documents ->
                val questionList = documents.mapNotNull { it.toObject<Question>() }
                questionsLiveData.value = questionList
            }
            .addOnFailureListener {
                questionsLiveData.value = emptyList() // Return an empty list on failure
            }

        return questionsLiveData
    }
}
