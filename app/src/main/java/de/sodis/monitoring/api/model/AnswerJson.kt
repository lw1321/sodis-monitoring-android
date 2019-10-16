package de.sodis.monitoring.api.model

data class AnswerJson(
    val id: Int? = null,
    val answerNumeric: Int? = null,
    val answerText: String? = null,
    val answerYn: Boolean? = null,
    val interviewee: Interviewee? = null,
    val questionOption: QuestionOption? = null
) {
    data class Interviewee(
        val id: Int,
        val name: String? = null
    )

    data class QuestionOption(
        val id: Int,
        val name: String? = null
    )
}