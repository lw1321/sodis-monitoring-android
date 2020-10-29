package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stats(
    @PrimaryKey
    val id: Int,
    var modificationDate: Long
)