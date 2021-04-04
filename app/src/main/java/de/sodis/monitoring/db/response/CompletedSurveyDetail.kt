package de.sodis.monitoring.db.response

data class CompletedSurveyDetail(
    val answerText: String?,
    val questionName: String,
    val sectionName: String,
    val inputTypeId: Int,
    val path: String?
)