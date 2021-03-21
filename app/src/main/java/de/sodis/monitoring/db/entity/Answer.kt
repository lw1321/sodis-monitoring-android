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
        ),
        ForeignKey(
            entity = QuestionOption::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("questionOptionId")
        ),
        ForeignKey(
            entity = Question::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("questionId")
        )
    ]
)
data class Answer(
    @PrimaryKey val id: String,
    val answerText: String?,
    val questionOptionId: Int?,
    val questionId: Int,
    var completedSurveyId: String?,
    var imagePath: String?,
    var imageSynced: Boolean?//TODO
)