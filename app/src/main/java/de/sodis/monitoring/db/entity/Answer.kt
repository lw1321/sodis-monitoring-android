package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Interviewee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("intervieweeId")
            ),
        ForeignKey(
            entity = QuestionOption::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("questionOptionId")
        )
    ]
)
data class Answer(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val answerNumeric: Int?,
    val answerText: String?,
    val answerYn: Boolean?,
    val intervieweeId: Int,
    val questionOptionId: Int?,
    val timeStamp: String,
    var submitted: Boolean = false
    //TODO GPS position
    )