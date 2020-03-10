package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import de.sodis.monitoring.api.model.SurveyHeaderJson


@Entity(
    foreignKeys = [ForeignKey(
        entity = SurveyHeader::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("surveyHeaderId")
    ), ForeignKey(
        entity = IntervieweeTechnology::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("intervieweeTechnologyId")
    )]
)
data class Task(
    @PrimaryKey
    val id: Int,
    val name: String? = null,
    val intervieweeTechnologyId: Int? = null,
    val type: Int? = null,
    val completedOn: String? = null,  //todo date time,
    val surveyHeaderId: Int? = null
)