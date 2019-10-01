package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.Interviewee

@Dao
interface IntervieweeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(interviewee: Interviewee)

    @Query("SELECT * FROM Interviewee")
    fun getAll(): LiveData<List<Interviewee>>

    @Query("SELECT * FROM Interviewee WHERE name=:name")
    fun getByName(name: String): LiveData<Interviewee>
}
