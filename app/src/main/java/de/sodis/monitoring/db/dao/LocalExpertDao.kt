package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.LocalExpert
import de.sodis.monitoring.db.entity.Sector

@Dao
interface LocalExpertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(localExpert: LocalExpert)

    @Query("SELECT * FROM LocalExpert")
    fun getAll(): LiveData<List<LocalExpert>>

    @Query("SELECT * FROM LocalExpert WHERE id=:localExpertId")
    fun getById(localExpertId: Int): LocalExpert

    @Query("SELECT COUNT(*) FROM LocalExpert WHERE id=:localExpertId")
    fun count(localExpertId: Int): Int
}