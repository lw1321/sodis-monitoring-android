package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.Interviewee

@Dao
interface IntervieweeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(interviewee: Interviewee)

    @Query("SELECT * FROM Interviewee")
    fun getAll(): LiveData<List<Interviewee>>

    @Query("SELECT * FROM Interviewee WHERE name=:name")
    fun getByName(name: String): LiveData<Interviewee>

    @Query("SELECT * FROM Interviewee WHERE name LIKE ''%:name%''")
    fun searchByName(name: String): List<Interviewee>

    @Query("SELECT * FROM Interviewee WHERE villageId=:villageId ORDER BY name")
    fun getByVillage(villageId: Int): LiveData<List<Interviewee>>

    @Query("SELECT * FROM Interviewee WHERE id=:intervieweeId")
    fun getById(intervieweeId: Int): Interviewee

    @Update
    fun update(interviewee: Interviewee)

    @Query("SELECT * FROM Interviewee WHERE synced=0")
    fun getAllNotSynced(): List<Interviewee>
}
