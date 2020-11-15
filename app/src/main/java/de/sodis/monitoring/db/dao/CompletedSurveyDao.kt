package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.CompletedSurvey
import de.sodis.monitoring.db.response.CompletedSurveyOverview

@Dao
interface CompletedSurveyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(completedSurvey: CompletedSurvey): Long

    @Query("SELECT * FROM CompletedSurvey WHERE submitted=0")
    fun getAllUnsubmitted(): List<CompletedSurvey>

    @Query("SELECT Interviewee.name, SurveyHeader.surveyName, CompletedSurvey.id FROM CompletedSurvey JOIN Interviewee ON CompletedSurvey.intervieweeId=Interviewee.id JOIN SurveyHeader ON CompletedSurvey.surveyHeaderId=SurveyHeader.id")
    fun getAll(): LiveData<List<CompletedSurveyOverview>>

    @Query("UPDATE CompletedSurvey SET submitted=1 WHERE id IN (:ids)")
    fun setSubmitted(ids: List<Int>)

    @Query("SELECT * FROM CompletedSurvey WHERE id=:id")
    fun getById(id: Int): CompletedSurvey

    @Query("SELECT Interviewee.name, SurveyHeader.surveyName, CompletedSurvey.id FROM CompletedSurvey JOIN Interviewee ON CompletedSurvey.intervieweeId=Interviewee.id JOIN SurveyHeader ON CompletedSurvey.surveyHeaderId=SurveyHeader.id ORDER BY timeStamp DESC")
    fun getAllSorted(): LiveData<List<CompletedSurveyOverview>>


}