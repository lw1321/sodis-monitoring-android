package de.sodis.monitoring.api.model

data class TaskJson(
    val id: Int,
    val name: String,
    val intervieweeTechnology: IntervieweeJson.IntervieweeTechnology,
    val type: Int,
    val completedOn: String,//todo datetime
    val surveyHeaderJson: SurveyHeaderJson?=null
)
