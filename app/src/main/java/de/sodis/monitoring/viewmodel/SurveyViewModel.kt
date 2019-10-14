package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.response.QuestionAnswer
import de.sodis.monitoring.db.response.SurveyHeaderResponse
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.QuestionRepository
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
    private lateinit var interviewee: LiveData<Interviewee>
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

    lateinit var surveyQuestions: List<QuestionAnswer>

    /**
     * holds all ui relevant informations for the questions
     */
    val questionItemList: MediatorLiveData<List<QuestionAnswer>> = MediatorLiveData()
    /**
     * Repository for interviewee actions
     */
    private val surveyHeaderRepository =
        SurveyHeaderRepository(
            surveyHeaderDao = MonitoringDatabase.getDatabase(application.applicationContext).surveyHeaderDao()
        )
    private val questionRepository =
        QuestionRepository(
            questionDao = MonitoringDatabase.getDatabase(application.applicationContext).questionDao(),
            questionOptionDao = MonitoringDatabase.getDatabase(application.applicationContext).questionOptionDao(),
            questionImageDao = MonitoringDatabase.getDatabase(application.applicationContext).questionImageDao()
        )

    /**
     * current position in questionaire
     */
    var currentPosition: Int = 0

    var answerMap = mutableMapOf<Int, String>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            intervieweeList = intervieweeRepository.getAll()
            surveyHeader = surveyHeaderRepository.getSurveyById(surveyId)

            viewModelScope.launch(Dispatchers.Main) {
                questionItemList.addSource(surveyHeader) {
                    //we got the survey headers! not we can query the questions.
                    viewModelScope.launch(Dispatchers.IO) {
                        surveyQuestions = questionRepository.getQuestionsBySurveySections(
                            surveyHeader.value!!.surveyHeader.surveyName,
                            surveyHeader.value!!.surveySectionList.map { sectionItem -> sectionItem.id })
                        //now we have everything.., check if surveyQuestions is loaded sync, then generate
                        questionItemList.postValue(surveyQuestions)
                    }
                }
            }
        }
    }

    fun setInterviewee(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interviewee = intervieweeRepository.getByName(name = text)
        }
    }

    fun setAnswer(id: Int, answer: String) {
        answerMap[id] = answer
    }

    /**
     * increases the adapter position if possible, else starting saving routine
     */
    fun nextQuestion(): Boolean{
        if (currentPosition == (surveyQuestions.size - 1)) {
            //done with the survey, save the input
            //TODO
            questionRepository.saveQuestions(answerMap, interviewee.value!!)
            return false
        }
        currentPosition++
        //todo reset questionitemlist?! why list..item better
        return true
    }

}
