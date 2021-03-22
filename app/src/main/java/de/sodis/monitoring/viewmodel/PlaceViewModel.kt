package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.db.response.FamilyList
import de.sodis.monitoring.db.response.IntervieweeDetail
import de.sodis.monitoring.repository.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlaceViewModel(application: Application) : AndroidViewModel(application) {

    var familyList: LiveData<List<FamilyList>>
    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    private val placeRepository =
        PlaceRepository(
            intervieweeDao = monitoringDatabase.intervieweeDao(),
            villageDao = monitoringDatabase.villageDao(),
            userDao = monitoringDatabase.userDao(),
            monitoringApi = MonitoringApi()
        )

    init {
        familyList = placeRepository.getFamilyList()
    }

    fun storeImagePath(currentPhotoPath: String) {
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
