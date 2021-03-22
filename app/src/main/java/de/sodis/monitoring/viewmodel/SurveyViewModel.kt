package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.response.SurveyList
import de.sodis.monitoring.repository.SurveyRepository


class SurveyViewModel(
    private val mApplication: Application
) : AndroidViewModel(mApplication) {

    fun nextQuestion(): Boolean {
        TODO("Not yet implemented")
    }

    //neccessary to check if question is answered. or check via ui?
    fun isAnswered(id: Int): Boolean {
        TODO("Not yet implemented")
    }
    fun setAnswer(Answer:Answer){
        TODO("Not implemented")
    }
    var surveyList: LiveData<List<SurveyList>>

    /**
     * Repository for interviewee actions
     */
    val db = MonitoringDatabase.getDatabase(context = mApplication.applicationContext)
    val surveyRepository =
        SurveyRepository(
            inputTypeDao = db.inputTypeDao(),
            optionChoiceDao = db.optionChoiceDao(),
            questionDao = db.questionDao(),
            questionOptionDao = db.questionOptionDao(),
            surveyHeaderDao = db.surveyHeaderDao(),
            surveySectionDao = db.surveySectionDao(),
            questionImageDao = db.questionImageDao(),
            answerDao =  db.answerDao(),
            completedSurveyDao = db.completedSurveyDao(),
            monitoringApi = MonitoringApi()
        )
        init {
            surveyList = surveyRepository.getSurveyList()
        }

    /**
     *Location Client, will be initialized when permission is granted
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient


}
