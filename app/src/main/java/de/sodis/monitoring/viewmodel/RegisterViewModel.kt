package de.sodis.monitoring.viewmodel

import android.app.Application
import de.sodis.monitoring.api.MonitoringApi
import androidx.lifecycle.*
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.User
import de.sodis.monitoring.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(monitoringApi = MonitoringApi(), userDao = MonitoringDatabase.getDatabase(application.applicationContext).userDao())

    //TODO first save local in case there is no internet, then upload via upload worker
    fun register(firstName: String, lastName: String, type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.register(
                User(
                    firstName = firstName,
                    lastName = lastName,
                    type = type
                )
            )
        }
    }
}
