package de.sodis.monitoring.api.models

data class SectionJson(
    val id: Int,
    val sectionName: String,
    val surveyHeader: SurveyHeader
) {
    data class SurveyHeader(
        val id: Int,
        val name: String
    )
}