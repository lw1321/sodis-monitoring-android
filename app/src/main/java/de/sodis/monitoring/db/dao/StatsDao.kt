package de.sodis.monitoring.db.dao

import androidx.room.*
import de.sodis.monitoring.db.entity.Stats

@Dao
interface StatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stat: Stats)

    @Update
    fun update(stat:Stats)

    @Query("SELECT * FROM Stats WHERE id=:statId")
    fun getById(statId: Int): Stats
}