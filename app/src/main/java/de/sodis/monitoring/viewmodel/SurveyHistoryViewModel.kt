package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.response.CompletedSurveyDetail
import de.sodis.monitoring.db.response.CompletedSurveyOverview
import de.sodis.monitoring.repository.SurveyHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SurveyHistoryViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * current position in questionaire
     */
    var currentPosition: Int = 0

    var surveyHistoryList: LiveData<List<CompletedSurveyOverview>>
    var surveyCompletedList: MutableLiveData<List<CompletedSurveyDetail>>

    val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    val surveyHistoryRepository =
        SurveyHistoryRepository(
            completedSurveyDao = monitoringDatabase.completedSurveyDao(),
            surveyHeaderDao = monitoringDatabase.surveyHeaderDao(),
            answerDao = monitoringDatabase.answerDao(),
            questionDao = monitoringDatabase.questionDao(),
            imageDao = monitoringDatabase.questionImageDao()
        )

    init {
        surveyHistoryList = surveyHistoryRepository.getCompletedSurveys()
        surveyCompletedList = MutableLiveData()
    }

    fun setCompletedSurveyId(completedSurveyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            surveyCompletedList.postValue(surveyHistoryRepository.getCompletedSurvey(completedSurveyId))
        }
    }

    fun getCompleteSurveyList(completedSurveyID: Int): List<CompletedSurveyDetail> {
        return surveyHistoryRepository.getCompletedSurvey(completedSurveyID)
    }

    fun getCompletedSurvey(): List<CompletedSurveyDetail>? {
        return surveyCompletedList.value;
    }

    fun previousQuestion() {
        currentPosition--
    }

    fun nextQuestion(): Boolean {
        if (currentPosition == (surveyCompletedList.value!!.size - 1)) {
            currentPosition = 0
            return false
        }
        currentPosition++
        return true
    }

}
