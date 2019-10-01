package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.response.SurveyHeaderResponse
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.SurveyHeaderRepository
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
    lateinit var questions: LiveData<Question>

    /**
     * Repository for interviewee actions
     */
    private val surveyHeaderRepository =
        SurveyHeaderRepository(
            surveyHeaderDao = MonitoringDatabase.getDatabase(application.applicationContext).surveyHeaderDao()
        )
    init {
        viewModelScope.launch(Dispatchers.IO) {
            intervieweeList = intervieweeRepository.getAll()
            surveyHeader = surveyHeaderRepository.getSurveyById(surveyId)
            //TODO Joined respponse!!, or  query after query..sections, questions,..
            questions = surveyHeaderRepository.getQuestions(surveyHeader.value)
        }
    }
    fun setInterviewee(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interviewee =  intervieweeRepository.getByName(name = text)
        }
    }

}
