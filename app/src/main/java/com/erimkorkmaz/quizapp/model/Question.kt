package com.erimkorkmaz.quizapp.model

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("category") val category: String,
    @SerializedName("type") val type : String,
    @SerializedName("difficulty") val difficulty : String,
    @SerializedName("question") val questions: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers : List<String>
) {
}