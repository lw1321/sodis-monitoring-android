package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = Question::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("questionId")
        ),
        ForeignKey(
            entity = OptionChoice::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("optionChoiceId")
        )
    ]
)
data class QuestionOption(
    @PrimaryKey val id: Int,
    val questionId: Int,
    val optionChoiceId: Int
)