package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Technology::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("technologyId")
        ),
        ForeignKey(
            entity = Interviewee::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("intervieweeId")
        )
    ]
)
data class IntervieweeTechnology(
    @PrimaryKey val id: Int,
    val intervieweeId: Int,
    val technologyId: Int,
    var stateTechnology: Int,
    var stateKnowledge: Int
)