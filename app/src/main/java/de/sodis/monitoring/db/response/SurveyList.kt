package de.sodis.monitoring.db.response

data class SurveyList(
    val surveyId: Int,
    val surveyName: String,
    val projectId: Int,
    val projectName: String
)
