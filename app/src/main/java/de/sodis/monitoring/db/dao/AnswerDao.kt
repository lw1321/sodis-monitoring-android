package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.Answer

@Dao
interface AnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(answer: Answer)

    @Query("SELECT * FROM Answer WHERE submitted = 0")
    fun getAllUnsubmitted(): List<Answer>

    @Query("UPDATE ANSWER SET submitted=1 WHERE id IN (:ids)")
    fun setSubmitted(ids: List<Int>)

}
