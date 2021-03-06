package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Interviewee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("intervieweeId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SurveyHeader::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("surveyHeaderId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CompletedSurvey(
    @PrimaryKey val id: String,
    val intervieweeId: String,
    val surveyHeaderId: Int,
    val creationDate: String,
    var submitted: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
)