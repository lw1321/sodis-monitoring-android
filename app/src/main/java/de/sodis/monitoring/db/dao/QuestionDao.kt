package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.Question

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: Question)

    @Query("SELECT * FROM Question ")
    fun getAll(): List<Question>

    @Query("SELECT * FROM Question WHERE surveySectionId IN (:surveySections)")
    fun getBySurveySections(surveySections: List<Int>): List<Question>

    @Query("SELECT * FROM Question JOIN QuestionOption ON QuestionOption.questionId=Question.id WHERE QuestionOption.id=:questionOptionId")
    fun getByQuestionOptionId(questionOptionId: Int): Question

    @Query("DELETE FROM Question WHERE id not in (:ids)")
    fun deleteAllExcluded(ids: List<Int>)

}
