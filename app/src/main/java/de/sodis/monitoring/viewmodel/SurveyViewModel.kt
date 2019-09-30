package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.repository.IntervieweeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SurveyViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var intervieweeList: LiveData<List<Interviewee>>

    private val intervieweeRepository =
        IntervieweeRepository(
            intervieweeDao = MonitoringDatabase.getDatabase(application.applicationContext).intervieweeDao(),
            monitoringApi = MonitoringApi()
        )
    init {
        viewModelScope.launch(Dispatchers.IO) {
            intervieweeList = intervieweeRepository.getAll()
        }
    }

    val intervieewistDummy = listOf("Belgium", "France", "Italy", "Germany", "Spain")
}
