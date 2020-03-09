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
    val womenCount: Int
    )
