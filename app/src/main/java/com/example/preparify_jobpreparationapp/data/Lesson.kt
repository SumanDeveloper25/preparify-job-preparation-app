package com.example.preparify_jobpreparationapp.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lesson(
    var lessonId: String = "",
    var title: String = "",
    var subtitle: String = "",
    var lessonNo: Int = 0,
    var content: String = "",
    var createdBy: String = "",
    var created_at: Timestamp? = null,
    var videoUrl: String? = null,
    var isCompleted: Boolean = false,
    var score: String = ""
) : Parcelable
