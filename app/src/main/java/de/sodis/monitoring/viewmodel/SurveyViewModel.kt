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
import de.sodis.monitoring.db.response.QuestionItem
import de.sodis.monitoring.db.response.SurveyList
import de.sodis.monitoring.repository.SurveyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class SurveyViewModel(
    private val mApplication: Application
) : AndroidViewModel(mApplication) {

    var surveyId: Int = 0
    var position: Int = 0
    var intervieweeId: Int = 2

    lateinit var currentInterviewee: String

    fun setAnswer(questionOption: Int?, imagePath: String?, answerText: String?) {
        // create Answer Object, map it with the position and replace if answer at
        // this position already exists
        answerMap[currentQuestion.first().id] = Answer(
            id = UUID.randomUUID().toString(),
            questionId = currentQuestion.first().id,
            imagePath = imagePath,
            answerText = answerText,
            questionOptionId = questionOption,
            completedSurveyId = null,
            imageSynced = false,
            submitted = false
        )
    }

    fun startSurvey() {
        viewModelScope.launch(Dispatchers.IO) {
            //Get first question of survey
            //TODO check if survey finished
            val questionList = surveyRepository.getQuestionList(surveyId)
            val distinctQuestion = surveyRepository.getQuestionsDistinct(surveyId)
            if (distinctQuestion.size <= position) {
                question.postValue(null)
            } else {
                currentQuestion = questionList.filter { it.id == distinctQuestion[position] }
                question.postValue(currentQuestion)
            }
        }
    }

    fun requestLocationAndSaveSurvey() {
        //Get the current location, then save the survey
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
                        saveSurvey(location.longitude, location.latitude)
                    } else {
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

    private fun saveSurvey(longitude: Double? = null, latitude: Double? = null) {
        surveyRepository.saveCompletedSurvey(
            surveyHeaderId = surveyId,
            answerMap = answerMap,
            intervieweeId = currentInterviewee,
            longitude = longitude,
            latitude = latitude
        )
    }



    var question: MutableLiveData<List<QuestionItem>> = MutableLiveData()

    var surveyList: LiveData<List<SurveyList>>

    var completedSurveyList : LiveData<List<CompletedSurvey>>


    private lateinit var currentQuestion: List<QuestionItem>

    private val answerMap = mutableMapOf<Int, Answer>()

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
        completedSurveyList = surveyRepository.getAllCompletedSurveys()
    }


    /**
     *Location Client, will be initialized when permission is granted
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient


}
