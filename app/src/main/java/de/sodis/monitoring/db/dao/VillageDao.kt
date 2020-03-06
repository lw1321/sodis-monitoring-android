package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Village

@Dao
interface VillageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(village: Village)

    @Query("SELECT COUNT(*) FROM Village WHERE id = :id")
    fun count(id: Int): Int

}