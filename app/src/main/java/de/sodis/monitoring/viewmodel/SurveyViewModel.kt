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
                .technologyDao()
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

        fun setInterviewee(id: String) {
            //TODO implement
        }

        fun setAnswer(id: Int, answer: String, optionChoiceId: Int, imagePath: String? = null) {
            //TODO implement
        }

        /**
         * increases the adapter position if possible, else starting saving routine
         */
        fun nextQuestion(): Boolean {
            //TODO implement
        }

        fun saveSurvey(latitude: Double? = null, longitude: Double? = null) {
            //TODO implement
        }

        fun setSurveyId(surveyId: Int) {
            createQuestionList(surveyId)
        }

        fun isAnswered(id: Int): Boolean {
            //TODO implement
        }

        fun previousQuestion(): Boolean {
            //TODO implement
        }

        fun answerToID(id: Int): Answer? {
            //TODO implement
        }
    }


    //returns true if the answer is "Escribir en la lista de tareas"
    fun createTodo(): Boolean {
        //TODO implement
    }
}
