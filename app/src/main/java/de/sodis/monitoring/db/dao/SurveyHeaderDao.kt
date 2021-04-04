package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.db.response.SurveyHeaderResponse
import de.sodis.monitoring.db.response.SurveyList

@Dao
interface SurveyHeaderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(surveyHeader: SurveyHeader)

    @Update
    fun update(surveyHeader: SurveyHeader)

    @Delete
    fun deleteAll(surveyheader: List<SurveyHeader>)

    @Query("SELECT * FROM SurveyHeader")
    fun getAll(): LiveData<List<SurveyHeader>>

    @Query("SELECT * FROM SurveyHeader WHERE projectId=:projectId")
    fun getAllFilteredProject(projectId: Int): LiveData<List<SurveyHeader>>

    @Query("SELECT * FROM SurveyHeader WHERE projectId=:projectId")
    fun getAllFilteredProjectSync(projectId: Int): List<SurveyHeader>

    @Query("SELECT * FROM SurveyHeader WHERE id=:surveyHeaderId")
    fun getById(surveyHeaderId: Int): LiveData<SurveyHeaderResponse>

    @Query("SELECT * FROM SurveyHeader WHERE id=:surveyHeaderId")
    fun getByIdSync(surveyHeaderId: Int): SurveyHeaderResponse

    @Query("DELETE FROM SurveyHeader WHERE id not in (:ids)")
    fun deleteAllExcluded(ids: List<Int>)

    @Query("SELECT COUNT(*) FROM SurveyHeader WHERE id=:id")
    fun exists(id: Int): Int

    @Query("SELECT sh.surveyName, sh.id as surveyId, pr.id as projectId, pr.name as projectName  FROM SurveyHeader sh JOIN project pr ON pr.id = sh.projectId")
    fun getAllSurveys(): LiveData<List<SurveyList>>

}
