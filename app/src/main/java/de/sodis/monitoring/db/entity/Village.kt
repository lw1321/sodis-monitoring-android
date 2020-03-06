package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Village(
    @PrimaryKey(autoGenerate = true) val id:Int?,
    val name: String?
)