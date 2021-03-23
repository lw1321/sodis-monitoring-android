package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.response.QuestionItem
import de.sodis.monitoring.db.response.SurveyList
import de.sodis.monitoring.repository.SurveyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

    fun setAnswer(Answer: Answer) {
        TODO("Not implemented")
    }

    fun startSurvey(surveyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            questionList = surveyRepository.getQuestionList(surveyId)
            currentQuestionId = questionList.first().id
            question.postValue(questionList.filter { it.id == currentQuestionId })
        }
    }

    var currentQuestionId: Int? = null

    lateinit var question: MutableLiveData<List<QuestionItem>>

    var surveyList: LiveData<List<SurveyList>>
    private lateinit var questionList: List<QuestionItem>

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
            answerDao = db.answerDao(),
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
