package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.response.IntervieweeItem
import de.sodis.monitoring.repository.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlaceViewModel(application: Application) : AndroidViewModel(application) {

    var intervieweeItem: LiveData<List<IntervieweeItem>>
    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    private val placeRepository =
        PlaceRepository(
            intervieweeDao = monitoringDatabase.intervieweeDao(),
            villageDao = monitoringDatabase.villageDao(),
            userDao = monitoringDatabase.userDao(),
            monitoringApi = MonitoringApi()
        )

    init {
        intervieweeItem = placeRepository.getFamilyList()
    }

    fun storeImagePath(currentPhotoPath: String, intervieweeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.storeIntervieweeImagePath(currentPhotoPath, intervieweeId)
        }
    }


    fun createInterviewee(name: String, village: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.createInterviewee(name, village)
        }
    }


    fun getByID(family: String): String {
        TODO("Not yet implemented")
    }

    fun getVillageByID(village: Int): String {
        TODO("Not yet implemented")
    }
}
