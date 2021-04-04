package de.sodis.monitoring.api.models

data class SurveyJson(
    val id: Int,
    val surveyName: String,
    val project: Project
) {
    data class Project(
        val id: Int,
        val name: String
    )
}