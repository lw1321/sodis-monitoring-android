package de.sodis.monitoring.api.model

data class CompletedSurveyJson(
    val id: String,
    val answers: List<Answer>,
    val creationDate: String,
    val interviewee: Interviewee,
    val surveyHeader: SurveyHeader,
    val latitude: Double?,
    val longitude: Double?
) {
    data class Answer(
        val id: String,
        val answerText: String?,
        val questionOption: QuestionOption//TODO add image path and synced?
    ) {
        data class QuestionOption(
            val id: Int?
        )
    }

    data class Interviewee(
        val id: String,
        val name: String,
        val village: Village
    ) {
        data class Village(
            val id: Int
        )
    }

    data class SurveyHeader(
        val id: Int
    )
}