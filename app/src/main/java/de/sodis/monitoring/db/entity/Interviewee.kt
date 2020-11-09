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
    val oldWomenCount: Int,
    var menCount: Int,
    val womenCount: Int,
    val userId: Int?,
    val sectorId: Int?,
    var imagePath: String?=null,
    var imageUrl: String?=null,
    var synced: Boolean?=true,
    wie mach ich das mit client id und server id.
    probleme: Verbindungen andere tables,
    Lösung: lokale Id erstellen server id abspeichern wenn hochgeladen. FK auf server_id ändern?
)
