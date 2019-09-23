package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OptionChoice(
    @PrimaryKey val id: Int,
    val optionChoiceName: String
)
