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
import de.sodis.monitoring.db.response.IntervieweeDetail
import de.sodis.monitoring.db.response.QuestionItem
import de.sodis.monitoring.db.response.SurveyHeaderResponse
import de.sodis.monitoring.db.response.SurveyList
import de.sodis.monitoring.repository.PlaceRepository
import de.sodis.monitoring.repository.SurveyRepository
import de.sodis.monitoring.repository.worker.UploadWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*


class QuestionViewModel(
    private val mApplication: Application,
    surveyId: Int
) : AndroidViewModel(mApplication) {


    /**
     * Selected Survey, joined sql Response
     */
    /**
     * Selected interviewee
     */
    var interviewee: IntervieweeDetail? = null

    lateinit var questionItemList: List<QuestionItem>

    lateinit var questionIdList: List<Int>

    /**
     * holds all ui relevant informations for the questions
     */
    val questionItemLiveList: MutableLiveData<List<QuestionItem>> = MutableLiveData()


    /**
     *Location Client, will be initialized when permission is granted
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient


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
    private val placeRepository =
        PlaceRepository(
            intervieweeDao = db.intervieweeDao(),
            villageDao = db.villageDao(),
            userDao = db.userDao(),
            monitoringApi = MonitoringApi()
        )

    /**
     * current position in questionaire
     */
    var currentPosition: MutableLiveData<Int> = MutableLiveData()

    var listOfAnsweredQuestions: List<Int> = mutableListOf()

    var answerMap = mutableMapOf<Int, Answer>()

    init {
        createQuestionList(surveyId)
        currentPosition.postValue(0)
    }

    private fun createQuestionList(surveyId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            questionIdList = surveyRepository.getQuestionsDistinct(surveyId)
            questionItemList = surveyRepository.getQuestionList(surveyId)
            questionItemLiveList.postValue(questionItemList)
        }
    }

    fun setInterviewee(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interviewee = placeRepository.getById(id)
        }
    }

    fun setAnswer(questionId: Int, questionOption: Int?, imagePath: String?, answerText: String?) {
        // create Answer Object, map it with the position and replace if answer at
        // this position already exists
        answerMap[questionId] = Answer(
            id = UUID.randomUUID().toString(),
            questionId = questionId,
            imagePath = imagePath,
            answerText = answerText,
            questionOptionId = questionOption,
            completedSurveyId = null,
            imageSynced = false,
            submitted = false
        )
    }

    /**
     * increases the adapter position if possible, else starting saving routine
     */
    fun finishSurvey() {
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
                        if (location != null) {
                            saveSurvey(location.latitude, location.longitude)
                        } else {
                            saveSurvey()
                        }
                    }.addOnFailureListener { it ->
                        //Location Request failed, save survey without location
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

        currentPosition.postValue(0)
        listOfAnsweredQuestions = mutableListOf()
    }

    fun nextQuestion(): Boolean {
        if (currentPosition.value!! == (questionIdList.size - 1)) {
            return false
        }
        currentPosition.postValue(currentPosition.value!! + 1)
        //check if current question has a  depended question. if so check if the depended question was
        //answered how excepected. If not call this function again. Recursive algorithm
        val questionItem = questionItemList.first { it.id == questionIdList[currentPosition.value!!] }
        if (questionItem.dependentQuestionId != 0) {
            //ok there is a depended question, so lets check the answer
            if (answerMap[questionItem.dependentQuestionId]?.questionOptionId != questionItem.dependentQuestionOptionId) {
                return nextQuestion()
            }
        }
        return true
    }

    private fun saveSurvey(latitude: Double? = null, longitude: Double? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            surveyRepository.saveCompletedSurvey(
                answerMap,
                intervieweeId = interviewee!!.interviewee.id,
                latitude = latitude,
                longitude = longitude
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
        return false
    }

    fun previousQuestion(): Boolean {
        if (currentPosition.value!! != 0) {
            val lastPosition = listOfAnsweredQuestions.last()
            answerMap.remove(questionIdList[lastPosition])
            listOfAnsweredQuestions =
                listOfAnsweredQuestions.subList(0, listOfAnsweredQuestions.size - 1)
            currentPosition.postValue(lastPosition)
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
        if (!answerMap.contains(questionIdList[currentPosition.value!!])) {
            return false
        }
        return answerMap[questionIdList[currentPosition.value!!]]!!.answerText.equals(
            "Escribir en la lista de tareas"
        ) //todo be aware of translation changes...
    }


}
