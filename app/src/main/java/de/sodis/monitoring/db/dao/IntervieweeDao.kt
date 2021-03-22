package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.response.FamilyList

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
    fun getById(intervieweeId: String): Interviewee

    @Update
    fun update(interviewee: Interviewee)

    @Query("SELECT * FROM Interviewee WHERE synced=0")
    fun getAllNotSynced(): List<Interviewee>

    @Query("SELECT COUNT(*) FROM Interviewee WHERE id=:id")
    fun exists(id: String): Int

    @Query("SELECT * FROM Interviewee WHERE imagePath IS NOT NULL AND imageUrl IS NULL ")
    fun getNotsyncedProfilePictures(): List<Interviewee>

    @Query("SELECT i.id, i.name, i.imagePath, v.name as villageName, v.id as villageId FROM Interviewee i JOIN village v ON v.id = i.villageId")
    fun getFamilyList(): LiveData<List<FamilyList>>

}
