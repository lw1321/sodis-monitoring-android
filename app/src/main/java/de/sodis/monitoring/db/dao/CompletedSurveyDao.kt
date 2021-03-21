package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.CompletedSurveyDetail
import de.sodis.monitoring.db.response.CompletedSurveyOverview

@Dao
interface CompletedSurveyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(completedSurvey: CompletedSurvey)

    @Query("SELECT * FROM CompletedSurvey WHERE submitted=0")
    fun getAllUnsubmitted(): List<CompletedSurvey>

    @Query("SELECT Interviewee.name, SurveyHeader.surveyName, CompletedSurvey.id FROM CompletedSurvey JOIN Interviewee ON CompletedSurvey.intervieweeId=Interviewee.id JOIN SurveyHeader ON CompletedSurvey.surveyHeaderId=SurveyHeader.id")
    fun getAll(): LiveData<List<CompletedSurveyOverview>>

    @Query("UPDATE CompletedSurvey SET submitted=1 WHERE id IN (:ids)")
    fun setSubmitted(ids: List<String>)

    @Query("SELECT * FROM CompletedSurvey WHERE id=:id")
    fun getById(id: String): CompletedSurvey

    @Query("SELECT Interviewee.name, SurveyHeader.surveyName, CompletedSurvey.id FROM CompletedSurvey JOIN Interviewee ON CompletedSurvey.intervieweeId=Interviewee.id JOIN SurveyHeader ON CompletedSurvey.surveyHeaderId=SurveyHeader.id ORDER BY timeStamp DESC")
    fun getAllSorted(): LiveData<List<CompletedSurveyOverview>>

    @Query("SELECT a.answerText, q.questionName, s.sectionName, q.inputTypeId, qi.path as imagePath FROM Answer a  JOIN Question q  ON a.questionId = q.questionName  JOIN SurveySection s ON s.id = q.surveySectionId JOIN QuestionImage qi ON qi.id = q.questionImageId " )
    fun getAnswers(completedSurveyId: String): List<CompletedSurveyDetail>


}