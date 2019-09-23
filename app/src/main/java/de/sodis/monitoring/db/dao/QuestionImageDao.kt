package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import de.sodis.monitoring.db.entity.Question

@Dao
interface QuestionImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(inputType: Question)
}
