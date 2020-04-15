package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.response.QuestionAnswer
import de.sodis.monitoring.db.response.SurveyHeaderResponse
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.QuestionRepository
import de.sodis.monitoring.repository.SurveyHeaderRepository
import de.sodis.monitoring.repository.worker.UploadWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp


class SurveyViewModel(
    private val mApplication: Application,
    surveyId: Int
) : AndroidViewModel(mApplication) {

    /**
     * Selected Survey, joined sql Response
     */
    //TODO join room response header, sections, questions,
    /**
     * Selected interviewee
     */
    var interviewee: Interviewee? = null
    /**
     * List of all interviewee
     */
    lateinit var intervieweeList: LiveData<List<Interviewee>>
    /**
     * Repository for interviewee actions
     */
    private val intervieweeRepository =
        IntervieweeRepository(
            intervieweeDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).intervieweeDao(),
            monitoringApi = MonitoringApi(),
            villageDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).villageDao(),
            localExpertDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).localExpertDao(),
            sectorDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).sectorDao(),
            technologyDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).technologyDao(),
            intervieweeTechnologyDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).intervieweeTechnologyDao(),
            taskDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).taskDao()
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
            surveyHeaderDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).surveyHeaderDao()
        )
    private val questionRepository =
        QuestionRepository(
            questionDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).questionDao(),
            questionOptionDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).questionOptionDao(),
            questionImageDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).questionImageDao(),
            answerDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).answerDao(),
            optionChoiceDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).optionChoiceDao(),
            monitoringApi = MonitoringApi()
        )

    /**
     * current position in questionaire
     */
    var currentPosition: Int = 0

    var answerMap = mutableMapOf<Int, Answer>()

    init {
        createQuestionList(surveyId)
    }

    private fun createQuestionList(surveyId: Int) {
        intervieweeList = intervieweeRepository.getAll()
        surveyHeader = surveyHeaderRepository.getSurveyById(surveyId)

        viewModelScope.launch(Dispatchers.Main) {
            questionItemList.removeSource(surveyHeader)
            questionItemList.addSource(surveyHeader) {
                //we got the survey headers! not we can query the questions.
                viewModelScope.launch(Dispatchers.IO) {
                    surveyQuestions = questionRepository.getQuestionsBySurveySections(
                        surveyHeader.value!!.surveySectionList
                    )
                    //now we have everything.., check if surveyQuestions is loaded sync, then generate
                    questionItemList.postValue(surveyQuestions)
                }
            }
        }
    }

    fun setInterviewee(text: String) {
        interviewee = intervieweeList.value!!.first { interviewee -> interviewee.name == text }
    }

    fun setAnswer(id: Int, answer: String, optionChoiceId: Int) {
        //request questionOption for the answer
        answerMap[id] = Answer(
            intervieweeId = interviewee!!.id,
            answerText = answer,
            timeStamp = Timestamp(System.currentTimeMillis()).toString(),
            answerNumeric = null,
            answerYn = null,
            id = null,
            questionOptionId = optionChoiceId
        )
    }

    /**
     * increases the adapter position if possible, else starting saving routine
     */
    fun nextQuestion(): Boolean {
        if (currentPosition == (surveyQuestions.size - 1)) {
            //done with the survey, save the input
            viewModelScope.launch(Dispatchers.IO) {
                questionRepository.saveQuestions(answerMap)
                answerMap.clear()
                interviewee = null
            }
            //start worker manager
            val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>().setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).build()
            WorkManager.getInstance(mApplication.applicationContext).enqueue(uploadWorkRequest)

            currentPosition = 0
            return false
        }
        currentPosition++
        //check if current question has a  depended question. if so check if the depended question was
        //answered how excepected. If not call this function again. Recursive algorithm
        val currentQuestion = questionItemList.value!![currentPosition].question
        if (currentQuestion.dependentQuestionId != 0) {
            //ok there is a depended question, so lets check the answer
            if (answerMap[currentQuestion.dependentQuestionId]?.questionOptionId != currentQuestion.dependentQuestionOptionId) {
                return nextQuestion()
            }
        }
        return true
    }

    fun setSurveyId(surveyId: Int) {
         createQuestionList(surveyId)
    }

    fun isAnswered(id: Int): Boolean = answerMap.containsKey(id)
}
