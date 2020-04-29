package de.sodis.monitoring.db.dao

import androidx.room.*
import de.sodis.monitoring.db.entity.QuestionImage

@Dao
interface QuestionImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionImage: QuestionImage)

    @Query("SELECT COUNT(*) FROM QuestionImage WHERE id = :id")
    fun exists(id: Int): Int

    @Query("SELECT * FROM QuestionImage WHERE id=:id")
    fun getById(id: Int): QuestionImage

    @Query("SELECT * FROM QuestionImage WHERE path is Null")
    fun getAllNotDownloaded(): List<QuestionImage>

    @Update
    fun update(questionImage: QuestionImage)
}
