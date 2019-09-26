package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.repository.SurveyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RootViewModel(application: Application) : AndroidViewModel(application) {

    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    private val surveyRepository =
        SurveyRepository(
            inputTypeDao = monitoringDatabase.inputTypeDao(),
            optionChoiceDao = monitoringDatabase.optionChoiceDao(),
            questionDao = monitoringDatabase.questionDao(),
            questionImageDao = monitoringDatabase.questionImageDao(),
            questionOptionDao = monitoringDatabase.questionOptionDao(),
            surveyHeaderDao = monitoringDatabase.surveyHeaderDao(),
            surveySectionDao = monitoringDatabase.surveySectionDao(),
            monitoringApi = MonitoringApi()
        )

    init {
        viewModelScope.launch(Dispatchers.IO){
            surveyRepository.loadSurveys(application.applicationContext)
        }
    }
}