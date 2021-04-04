package de.sodis.monitoring.api.models

import androidx.room.PrimaryKey

data class AnswerJson(
    val id: String,
    val answerText: String? = null,
    var questionOption: QuestionOption? = null,
    val question: Question,
    var completedSurvey: CompletedSurvey
) {
    data class Question(
        val id: Int
    )
    data class QuestionOption(
        val id: Int?
    )
    data class CompletedSurvey(
        val id: String
    )
}
