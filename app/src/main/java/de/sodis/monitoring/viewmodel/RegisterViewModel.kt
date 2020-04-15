package de.sodis.monitoring.viewmodel

import android.app.Application
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.db.response.IntervieweeDetail
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.SurveyRepository
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.lifecycle.*
import de.sodis.monitoring.api.model.UserRegister
import de.sodis.monitoring.db.dao.TaskDao
import de.sodis.monitoring.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(monitoringApi = MonitoringApi())

    //TODO first save local in case there is no internet, then upload via upload worker
    fun register(firstName: String, lastName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.register(UserRegister(firstName= firstName, lastName = lastName, type = 0))
        }
    }
}
