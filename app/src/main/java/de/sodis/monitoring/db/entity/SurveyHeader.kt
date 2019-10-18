package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SurveyHeader(
    @PrimaryKey
    val id: Int,
    val instructions: String?=null,
    val otherHeaderInfo: String?=null,
    val surveyName: String
)