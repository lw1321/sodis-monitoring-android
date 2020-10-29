package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Interviewee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("intervieweeId")
        ),
        ForeignKey(
            onDelete = ForeignKey.SET_NULL,
            entity = SurveyHeader::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("surveyHeaderId")
        )
    ]
)
data class CompletedSurvey(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val intervieweeId: Int,
    val surveyHeaderId: Int,
    val timeStamp: String,
    var submitted: Boolean = false
//TODO GPS position

)