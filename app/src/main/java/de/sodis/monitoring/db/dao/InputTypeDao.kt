package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.InputType

@Dao
interface InputTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(inputType: InputType)

    @Query("SELECT COUNT(*) FROM InputType WHERE id=:id")
    fun exists(id: Int): Int

    @Query("DELETE FROM InputType")
    fun deleteAll()
}
