package de.sodis.monitoring.api.model

data class CompletedSurveyJson(
    val answers: List<Answer>,
    val creationDate: String,
    val interviewee: Interviewee,
    val surveyHeader: SurveyHeader
) {
    data class Answer(
        val answerNumeric: Int?,
        val answerText: String?,
        val questionOption: QuestionOption
    ) {
        data class QuestionOption(
            val id: Int
        )
    }

    data class Interviewee(
        val id: Int
    )

    data class SurveyHeader(
        val id: Int
    )
}