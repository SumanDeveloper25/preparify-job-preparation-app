package com.example.preparify_jobpreparationapp.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp


data class Question(
    var courseId: String = "",
    var lessonId: String = "",
    var correctAnswers: List<String> = listOf(),
    var createdAt: Timestamp? = null,
    var createdBy: String = "",
    var explanation: String = "",
    var options: List<String> = listOf(),
    var question: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        courseId = parcel.readString() ?: "",
        lessonId = parcel.readString() ?: "",
        correctAnswers = parcel.createStringArrayList() ?: listOf(),
        createdAt = parcel.readParcelable(Timestamp::class.java.classLoader),
        createdBy = parcel.readString() ?: "",
        explanation = parcel.readString() ?: "",
        options = parcel.createStringArrayList() ?: listOf(),
        question = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(courseId)
        parcel.writeString(lessonId)
        parcel.writeStringList(correctAnswers)
        parcel.writeParcelable(createdAt, flags)
        parcel.writeString(createdBy)
        parcel.writeString(explanation)
        parcel.writeStringList(options)
        parcel.writeString(question)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}
