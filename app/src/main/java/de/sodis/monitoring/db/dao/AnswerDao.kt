package de.sodis.monitoring.db.dao

import androidx.room.*
import de.sodis.monitoring.db.entity.Answer

@Dao
interface AnswerDao {
    //todo insertAll
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(answer: Answer)

    @Update
    fun update(answer: Answer)

    @Query("SELECT * FROM Answer WHERE completedSurveyId=:completedSurveyId")
    fun getAnswersByCompletedSurveyId(completedSurveyId: String): List<Answer>

    @Query("SELECT Answer.* FROM Answer JOIN CompletedSurvey CS ON CS.id = Answer.completedSurveyId  WHERE imagePath IS NOT NULL AND imageSynced IS NULL AND submitted=1")
    fun getNotSubmittedImages(): List<Answer>

    @Query("SELECT * FROM Answer WHERE Answer.completedSurveyId IN (:ids)")
    fun getAllBySurveys(ids: List<String>): List<Answer>

    @Query("SELECT * FROM Answer WHERE Answer.submitted=0")
    fun getAllNotSubmitted(): List<Answer>

    @Query("SELECT * FROM Answer WHERE Answer.imageSynced=0")
    fun getAllImageNotSynced(): List<Answer>

}
