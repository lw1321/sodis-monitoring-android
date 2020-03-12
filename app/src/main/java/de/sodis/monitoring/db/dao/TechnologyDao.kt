package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Technology
import de.sodis.monitoring.db.entity.Village

@Dao
interface TechnologyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(technology: Technology)

    @Query("SELECT COUNT(*) FROM Technology WHERE id=:id")
    fun count(id: Int): Int

    @Query("SELECT * FROM Technology")
    fun getAll(): LiveData<List<Technology>>

}