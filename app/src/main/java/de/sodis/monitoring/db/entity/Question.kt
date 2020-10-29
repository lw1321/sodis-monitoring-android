package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = SurveySection::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("surveySectionId")),
        ForeignKey(
            entity = QuestionImage::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("questionImageId")),
        ForeignKey(
            entity = InputType::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("inputTypeId")
        )
    ]
)
data class Question(
    @PrimaryKey
    val id: Int,
    val dependentQuestionId: Int?,
    val dependentQuestionOptionId: Int?,
    val inputTypeId: Int,
    val questionName: String,
    val surveySectionId: Int,
    val questionImageId: Int
)