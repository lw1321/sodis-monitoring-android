package de.sodis.monitoring.db.response

data class CompletedSurveyItem(
        val id:String,
        val interviewee: String,
        val surveyName: String,
        val date: String,
        val village: String
)
