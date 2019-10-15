package de.sodis.monitoring.api.model

data class AnswerJson(
    val answerNumeric: Int? = null,
    val answerText: String? = null,
    val answerYn: Boolean? = null,
    val interviewee: Interviewee? = null,
    val questionOption: QuestionOption? = null
) {
    data class Interviewee(
        val Id: Int
    )

    data class QuestionOption(
        val Id: Int
    )
}