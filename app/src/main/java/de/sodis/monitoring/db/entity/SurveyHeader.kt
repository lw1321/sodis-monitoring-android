package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import de.sodis.monitoring.api.model.SurveyHeaderJson


@Entity(
    foreignKeys = [ForeignKey(
        entity = Technology::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("technologyId")
    )]
)
data class SurveyHeader(
    @PrimaryKey
    val id: Int,
    val instructions: String? = null,
    val otherHeaderInfo: String? = null,
    val surveyName: String,
    val technologyId: Int
)