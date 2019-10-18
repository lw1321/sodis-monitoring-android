package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuestionImage(
    @PrimaryKey
    val id: Int,
    val url: String,
    val path: String?
)