package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.db.dao.SurveyHeaderDao
import de.sodis.monitoring.db.entity.SurveyHeader

class SurveyHeaderRepository(
    private val surveyHeaderDao: SurveyHeaderDao
) {
    fun getSurveyById(surveyHeaderId: Int): SurveyHeader {
        return surveyHeaderDao.getById(surveyHeaderId)
    }

}
