package de.sodis.monitoring.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: Int? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val type: Int = 0,
    val firebaseId: String? = null,
    val email: String? = null
)
