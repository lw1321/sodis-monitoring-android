package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.repository.SurveyRepository

class MonitoringOverviewModel(application: Application) : AndroidViewModel(application) {

    var surveyHeaderList: LiveData<List<SurveyHeader>>
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
        surveyHeaderList = surveyRepository.getSurveyHeaders()
    }
}
