package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.Question

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: Question)

    @Query("SELECT * FROM Question ")
    fun getAll(): List<Question>

    @Query("SELECT * FROM Question WHERE surveySectionId IN (:surveySections)")
    fun getBySurveySections(surveySections: List<Int>): List<Question>
}
