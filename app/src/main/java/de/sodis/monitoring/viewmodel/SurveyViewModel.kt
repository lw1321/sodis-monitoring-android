package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.response.SurveyHeaderResponse
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.SurveyHeaderRepository
import de.sodis.monitoring.ui.model.QuestionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SurveyViewModel(
    application: Application,
    surveyId: Int
) : AndroidViewModel(application) {

    /**
     * Selected Survey, joined sql Response
     */
        //TODO join room response header, sections, questions,
    /**
     * Selected interviewee
     */
    private lateinit var  interviewee: LiveData<Interviewee>
    /**
     * List of all interviewee
     */
    lateinit var intervieweeList: LiveData<List<Interviewee>>
    /**
     * Repository for interviewee actions
     */
    private val intervieweeRepository =
        IntervieweeRepository(
            intervieweeDao = MonitoringDatabase.getDatabase(application.applicationContext).intervieweeDao(),
            monitoringApi = MonitoringApi()
        )
    lateinit var surveyHeader: LiveData<SurveyHeaderResponse>

    /**
     * holds all ui relevant informations for the questions
     */
    val questionItemList: MediatorLiveData<List<QuestionItem>> = MediatorLiveData()
    /**
     * Repository for interviewee actions
     */
    private val surveyHeaderRepository =
        SurveyHeaderRepository(
            surveyHeaderDao = MonitoringDatabase.getDatabase(application.applicationContext).surveyHeaderDao()
        )
    private val questionRepository =
        QuestionRepository(
            questionDao = MonitoringDatabase.getDatabase(application.applicationContext).questionDao()
        )
    init {
        viewModelScope.launch(Dispatchers.IO) {
            intervieweeList = intervieweeRepository.getAll()
            surveyHeader = surveyHeaderRepository.getSurveyById(surveyId)
            surveyQuestions = questionRepository.getQuestionsBySurveyHeader(surveyId)

            viewModelScope.launch(Dispatchers.Main){
                questionItemList.addSource(surveyHeader, Observer {
                    viewModelScope.launch(Dispatchers.IO) {
                        surveyQuestions = questionRepository.getQuestionsBySurveyHeader(surveyId)
                    }
                })
            }
        }
    }
    fun setInterviewee(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interviewee =  intervieweeRepository.getByName(name = text)
        }
    }

}
