package de.sodis.monitoring.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.db.response.SurveyHeaderResponse

@Dao
interface SurveyHeaderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(surveyHeader: SurveyHeader)

    @Query("SELECT * FROM SurveyHeader")
    fun getAll(): LiveData<List<SurveyHeader>>

    @Query("SELECT * FROM SurveyHeader WHERE id=:surveyHeaderId")
    fun getById(surveyHeaderId: Int): LiveData<SurveyHeaderResponse>

    @Query("SELECT * FROM SurveyHeader WHERE id=:surveyHeaderId")
    fun getByIdSync(surveyHeaderId: Int): SurveyHeaderResponse
}
