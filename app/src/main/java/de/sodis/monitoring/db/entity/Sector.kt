package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Village::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("villageId")
        )
    ]
)
data class Sector(
    @PrimaryKey
    val id: Int,
    val name: String,
    val villageId: Int
)