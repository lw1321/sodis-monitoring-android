package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(
    entity = SurveyHeader::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("surveyHeaderId"),
    onDelete = ForeignKey.CASCADE
)])
data class SurveySection(
    @PrimaryKey val id: Int,
    val sectionName: String,
    val surveyHeaderId: Int
)