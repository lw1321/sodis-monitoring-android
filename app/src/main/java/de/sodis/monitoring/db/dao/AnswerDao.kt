package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.Answer

@Dao
interface AnswerDao {
//todo insertAll
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(answer: Answer)

}
