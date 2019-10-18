package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InputType(
    @PrimaryKey val id: Int,
    val inputTypeName: String
)