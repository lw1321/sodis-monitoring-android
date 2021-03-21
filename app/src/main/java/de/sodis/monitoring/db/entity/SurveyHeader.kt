package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("technologyId")
    )]
)
data class SurveyHeader(
    @PrimaryKey
    val id: Int,
    val surveyName: String,
    val technologyId: Int
)