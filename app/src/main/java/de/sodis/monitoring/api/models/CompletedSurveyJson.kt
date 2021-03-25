package de.sodis.monitoring.api.models

import androidx.room.PrimaryKey

data class CompletedSurveyJson (
    val id: String,
    val interviewee: Interviewee,
    val surveyHeader: SurveyHeader,
    val timeStamp: String,
    var submitted: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    data class Interviewee (
        val id: String
    )
    data class SurveyHeader(
        val id: Int
    )
}