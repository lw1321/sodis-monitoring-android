package de.sodis.monitoring.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.CompletedSurvey
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
import java.util.*


class SurveyViewModel(
    private val mApplication: Application,
    surveyId: Int//TODO use args
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
     * Repository for interviewee actions
     */
    private val intervieweeRepository =
        IntervieweeRepository(
            intervieweeDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .intervieweeDao(),
            monitoringApi = MonitoringApi(),
            villageDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .villageDao(),
            userDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).userDao(),
            technologyDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .technologyDao(),
            intervieweeTechnologyDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .intervieweeTechnologyDao(),
            todoPointDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).todoPointDao()
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
            surveyHeaderDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .surveyHeaderDao()
        )
    private val questionRepository =
        QuestionRepository(
            questionDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .questionDao(),
            questionOptionDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .questionOptionDao(),
            questionImageDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .questionImageDao(),
            answerDao = MonitoringDatabase.getDatabase(mApplication.applicationContext).answerDao(),
            optionChoiceDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .optionChoiceDao(),
            completedSurveyDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .completedSurveyDao(),
            monitoringApi = MonitoringApi(),
            intervieweeTechnologyDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .intervieweeTechnologyDao(),
            surveyHeaderDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .surveyHeaderDao(),
            intervieweeDao = MonitoringDatabase.getDatabase(mApplication.applicationContext)
                .intervieweeDao()
        )

    /**
     *Location Client, will be initialized when permission is granted
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    /**
     * current position in questionaire
     */
    var currentPosition: Int = 0

    var listOfAnsweredQuestions: List<Int> = mutableListOf()

    var answerMap = mutableMapOf<Int, Answer>()

    init {
        createQuestionList(surveyId)
    }

    private fun createQuestionList(surveyId: Int) {
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

    fun setInterviewee(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interviewee = intervieweeRepository.getIntervieweeByID(id)
        }
    }

    fun setAnswer(id: Int, answer: String, optionChoiceId: Int) {
        //request questionOption for the answer
        answerMap[id] = Answer(
            answerText = answer,
            id = UUID.randomUUID().toString(),
            questionOptionId = optionChoiceId,
            completedSurveyId = null, //todo
            answerYn = null//todo differe yn/text
        )
    }

    /**
     * increases the adapter position if possible, else starting saving routine
     */
    fun nextQuestion(): Boolean {
        if (currentPosition == (surveyQuestions.size - 1)) {
            //done with the survey, save the input
            viewModelScope.launch(Dispatchers.IO) {
                // GET last know location
                if (ContextCompat.checkSelfPermission(
                        getApplication<Application>().applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(mApplication.applicationContext)
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            // Got last known location. In some rare situations this can be null.
                            if(location != null){
                                saveSurvey(location.latitude, location.longitude)
                            }
                            else{
                                saveSurvey()
                            }
                        }.addOnFailureListener { it ->
                            //Loation Request failed, save survey without location
                            saveSurvey()
                        }
                } else {
                    //Location not granted, save survey without location
                    saveSurvey()
                }
            }
            //start worker manager
            val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>().setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).build()
            WorkManager.getInstance(mApplication.applicationContext).enqueue(uploadWorkRequest)

            currentPosition = 0
            listOfAnsweredQuestions = mutableListOf()
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

    private fun saveSurvey(latitude: Double? = null, longitude: Double? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            questionRepository.saveQuestions(
                answerMap,
                CompletedSurvey(
                    id = UUID.randomUUID().toString(),
                    intervieweeId = interviewee!!.id,
                    timeStamp = Timestamp(System.currentTimeMillis()).toString(),
                    surveyHeaderId = surveyHeader.value!!.surveyHeader.id,
                    latitude = latitude,
                    longitude = longitude
                )
            )

            answerMap.clear()
            interviewee = null
        }

    }

    fun setSurveyId(surveyId: Int) {
        createQuestionList(surveyId)
    }

    fun isAnswered(id: Int): Boolean {
        if (answerMap.containsKey(id)) {
            return true
        }
        if (questionItemList.value!![currentPosition].question.inputTypeId == 3) {//numeric if not answered, set 0 todo question
            setAnswer(
                questionItemList.value!![currentPosition].question.id,
                0.toString(),
                questionItemList.value!![currentPosition].answers.first().questionOption.id
            )
            return true
        }
        //make razon questions optional, todo add bool requiered field to question
        if ((questionItemList.value!![currentPosition].question.questionName == "Razón")) {
            return true
        }
        return false
    }

    fun previousQuestion(): Boolean {
        if (currentPosition != 0) {
            val lastPosition = listOfAnsweredQuestions.last()
            answerMap.remove(questionItemList.value!![lastPosition].question.id)
            listOfAnsweredQuestions =
                listOfAnsweredQuestions.subList(0, listOfAnsweredQuestions.size - 1)
            currentPosition = lastPosition
            return true
        }
        return false
    }

    fun answerToID(id: Int): Answer? {
        return answerMap[id];
    }


    //returns true if the answer is "Escribir en la lista de tareas"
    fun createTodo(): Boolean {
        //optional questions
        if (! answerMap.contains(questionItemList.value!![currentPosition].question.id)) {
            return false
        }
        return answerMap[questionItemList.value!![currentPosition].question.id]!!.answerText.equals(
            "Escribir en la lista de tareas"
        ) //todo be aware of translation changes...
    }
}
