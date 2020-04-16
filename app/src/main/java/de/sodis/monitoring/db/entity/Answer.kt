package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CompletedSurvey::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("completedSurveyId")
        )
    ]
)
data class Answer(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val answerNumeric: Int?,
    val answerText: String?,
    val answerYn: Boolean?,
    val questionOptionId: Int,
    var completedSurveyId: Int?
)