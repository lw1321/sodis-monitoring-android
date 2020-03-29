package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.db.dao.CompletedSurveyDao
import de.sodis.monitoring.db.dao.TaskDao
import de.sodis.monitoring.db.entity.CompletedSurvey
import de.sodis.monitoring.db.entity.Task
import de.sodis.monitoring.db.response.CompletedSurveyOverview

class SurveyHistoryRepository(
    private val completedSurveyDao: CompletedSurveyDao
) {


    fun getCompletedSurveys(): LiveData<List<CompletedSurveyOverview>> {
        return completedSurveyDao.getAll()
    }



}