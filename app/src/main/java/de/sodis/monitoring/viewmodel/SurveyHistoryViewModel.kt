package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.CompletedSurveyOverview
import de.sodis.monitoring.repository.SurveyHistoryRepository


class SurveyHistoryViewModel(application: Application) : AndroidViewModel(application) {

    var surveyHistoryList: LiveData<List<CompletedSurveyOverview>>

    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    private val surveyHistoryRepository=
        SurveyHistoryRepository(
            completedSurveyDao = monitoringDatabase.completedSurveyDao()
        )

    init {
        surveyHistoryList= surveyHistoryRepository.getCompletedSurveys()
    }

}
