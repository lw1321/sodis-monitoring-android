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
    @PrimaryKey val id: String,
    val answerText: String?,
    val answerYn: Boolean?,
    val questionOptionId: Int,//TODO ADD FK!!
    var completedSurveyId: String?,
    var imagePath: String?,
    var imageSynced: Boolean?
)