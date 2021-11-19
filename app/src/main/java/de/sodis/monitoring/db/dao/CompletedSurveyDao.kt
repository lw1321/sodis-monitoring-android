package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.CompletedSurveyItem
import de.sodis.monitoring.db.response.CompletedSurveyOverview

@Dao
interface CompletedSurveyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(completedSurvey: CompletedSurvey)

    @Update
    fun update(completedSurvey: CompletedSurvey)

    @Query("SELECT * FROM CompletedSurvey WHERE submitted=0")
    fun getAllUnsubmitted(): List<CompletedSurvey>

    @Query("SELECT * FROM CompletedSurvey")
    fun getAll(): LiveData<List<CompletedSurvey>>

    @Query("UPDATE CompletedSurvey SET submitted=1 WHERE id IN (:ids)")
    fun setSubmitted(ids: List<String>)

    @Query("SELECT * FROM CompletedSurvey WHERE id=:id")
    fun getById(id: String): CompletedSurvey

    @Query("SELECT Interviewee.name, SurveyHeader.surveyName, CompletedSurvey.id FROM CompletedSurvey JOIN Interviewee ON CompletedSurvey.intervieweeId=Interviewee.id JOIN SurveyHeader ON CompletedSurvey.surveyHeaderId=SurveyHeader.id ORDER BY creationDate DESC")
    fun getAllSorted(): LiveData<List<CompletedSurveyOverview>>

    @Query("DELETE FROM CompletedSurvey")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM CompletedSurvey WHERE id IN (SELECT a.completedSurveyId FROM Answer a WHERE a.submitted=0)")
    fun getUnsubittedCountLive(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM CompletedSurvey WHERE id IN (SELECT a.completedSurveyId FROM Answer a WHERE a.submitted=0)")
    fun getUnsubittedCount(): Int

    @Query("SELECT cs.id, i.name, sh.surveyName, cs.creationDate, v.name FROM CompletedSurvey cs LEFT JOIN Interviewee i ON i.id=cs.intervieweeId LEFT JOIN Village v ON v.id=i.villageId LEFT JOIN SurveyHeader sh ON sh.id=cs.surveyHeaderId ")
    fun getAllSurveyItems(): LiveData<List<CompletedSurveyItem>>

}