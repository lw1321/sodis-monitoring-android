package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.db.dao.SurveyHeaderDao
import de.sodis.monitoring.db.response.SurveyHeaderResponse

class SurveyHeaderRepository(
    private val surveyHeaderDao: SurveyHeaderDao
) {
    fun getSurveyById(surveyHeaderId: Int): LiveData<SurveyHeaderResponse> {
        return surveyHeaderDao.getById(surveyHeaderId)
    }

}
