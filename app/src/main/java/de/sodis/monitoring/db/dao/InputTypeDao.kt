package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import de.sodis.monitoring.db.entity.InputType

@Dao
interface InputTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(inputType: InputType)
}
