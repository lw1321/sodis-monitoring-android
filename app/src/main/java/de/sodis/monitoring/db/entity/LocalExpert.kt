package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalExpert(
    @PrimaryKey
    val id: Int,
    val name: String
)