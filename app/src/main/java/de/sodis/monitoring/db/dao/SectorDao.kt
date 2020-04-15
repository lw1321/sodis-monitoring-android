package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.Sector

@Dao
interface SectorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sector: Sector)

    @Query("SELECT * FROM Sector")
    fun getAll(): LiveData<List<Sector>>

    @Query("SELECT * FROM Sector WHERE id=:sectorId")
    fun getById(sectorId: Int): Sector

    @Query("SELECT * FROM Sector WHERE villageId=:villageId")
    fun getByVillageId(villageId: Int): LiveData<List<Sector>>

    @Query("SELECT COUNT(*) FROM Sector WHERE id=:sectorId")
    fun count(sectorId: Int): Int
}