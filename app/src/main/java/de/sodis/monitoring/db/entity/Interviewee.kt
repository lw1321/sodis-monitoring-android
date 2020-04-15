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
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId")
        ),
        ForeignKey(
            entity = Sector::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sectorId")
        )
    ]
)
data class Interviewee(
    @PrimaryKey
    val id: Int,
    val name: String,
    val villageId: Int,
    val girlsCount: Int,
    val boysCount: Int,
    val youngMenCount: Int,
    val youngWomenCount: Int,
    val oldMenCount: Int,
    val hasKnowledge: Boolean,
    val oldWomenCount: Int,
    val menCount: Int,
    val womenCount: Int,
    val userId: Int?,
    val sectorId: Int?
)
