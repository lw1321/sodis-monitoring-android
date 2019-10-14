package de.sodis.monitoring.api.model

data class AnswerJson(
    val answerNumeric: Int,
    val answerText: String,
    val answerYn: Boolean,
    val interviewee: Interviewee,
    val questionOption: QuestionOption
) {
    data class Interviewee(
        val Id: Int
    )

    data class QuestionOption(
        val Id: Int
    )
}