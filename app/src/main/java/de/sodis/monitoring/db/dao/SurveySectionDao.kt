package de.sodis.monitoring.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.sodis.monitoring.db.entity.SurveySection
import retrofit2.http.DELETE

@Dao
interface SurveySectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(surveySection: SurveySection)

    @Query("DELETE FROM SurveySection WHERE id not in (:ids)")
    fun deleteAllExcluded(ids: List<Int>)

}
