package de.sodis.monitoring.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.CompletedSurvey
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.db.response.CompletedSurveyItem
import de.sodis.monitoring.db.response.QuestionItem
import de.sodis.monitoring.db.response.SurveyList
import de.sodis.monitoring.repository.PlaceRepository
import de.sodis.monitoring.repository.SurveyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class HistoryViewModel(
    private val mApplication: Application
) : AndroidViewModel(mApplication) {

    private val db = MonitoringDatabase.getDatabase(mApplication.applicationContext)
    private val surveyRepository =
            SurveyRepository(
                    inputTypeDao = db.inputTypeDao(),
                    optionChoiceDao = db.optionChoiceDao(),
                    questionDao = db.questionDao(),
                    questionOptionDao = db.questionOptionDao(),
                    surveyHeaderDao = db.surveyHeaderDao(),
                    surveySectionDao = db.surveySectionDao(),
                    questionImageDao = db.questionImageDao(),
                    answerDao = db.answerDao(),
                    completedSurveyDao = db.completedSurveyDao(),
                    monitoringApi = MonitoringApi()
            )

    var unsyncedSurveyCountLive: LiveData<Int>
    var unsyncedSurveyCount : Int = -1
    var notSubmittedSurveyItems: LiveData<List<CompletedSurveyItem>>
    var allSurveyItems: LiveData<List<CompletedSurveyItem>>


    init {
        unsyncedSurveyCountLive = surveyRepository.getUnsyncedSurveyCountLive()
        allSurveyItems = surveyRepository.getCompletedSurveyItem()
        notSubmittedSurveyItems = surveyRepository.getAllSurveyItemsUnsubmitted()

        viewModelScope.launch(Dispatchers.IO) {
            unsyncedSurveyCount=surveyRepository.getUnsyncedSurveyCount()
        }

    }
}
