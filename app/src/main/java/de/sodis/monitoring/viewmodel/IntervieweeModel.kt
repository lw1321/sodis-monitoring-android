package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.Sector
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.db.response.IntervieweeDetail
import de.sodis.monitoring.repository.IntervieweeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class IntervieweeModel(application: Application) : AndroidViewModel(application) {

    var intervieweeList: LiveData<List<Interviewee>>
    var villageList: LiveData<List<Village>>
    var intervieweeDetail: MutableLiveData<IntervieweeDetail>

    var modiefied: Boolean = false

    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    private val intervieweeRepository =
        IntervieweeRepository(
            intervieweeDao = monitoringDatabase.intervieweeDao(),
            villageDao = monitoringDatabase.villageDao(),
            sectorDao = monitoringDatabase.sectorDao(),
            userDao = monitoringDatabase.userDao(),
            intervieweeTechnologyDao = monitoringDatabase.intervieweeTechnologyDao(),
            technologyDao = monitoringDatabase.technologyDao(),
            taskDao = monitoringDatabase.taskDao(),
            monitoringApi = MonitoringApi()
        )

    init {
        intervieweeList = intervieweeRepository.getAll()
        villageList = intervieweeRepository.getAllVillages()
        intervieweeDetail = MutableLiveData()
    }

    fun getByVillage(villageId: Int): LiveData<List<Interviewee>> {
        return intervieweeRepository.getByVillage(villageId)
    }

    fun setInterviewee(intervieweeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            intervieweeDetail.postValue(intervieweeRepository.getById(intervieweeId))
        }
    }

    fun getByID(intervieweeId: Int): Interviewee {
        return intervieweeRepository.getIntervieweeByID(intervieweeId)
    }

    fun updateInterviewee(interviewee: Interviewee) {
        intervieweeDetail.value!!.interviewee = interviewee
        modiefied = true
    }

    fun getSectorsOfVillage(villageId: Int): Array<CharSequence> {
        val sectorsInVillage = intervieweeRepository.getSectorsOfVillage(villageId)
        return sectorsInVillage.value!!.map { it.name }.toTypedArray()

    }

    fun saveInterviewee() {
        intervieweeDetail.value!!.interviewee.changed = true
        viewModelScope.launch(Dispatchers.IO) {
            intervieweeRepository.saveInterviewee(intervieweeDetail.value!!.interviewee)
        }
    }

    fun getVillageByID(int: Int): Village {
        return intervieweeRepository.getVillageByID(int)
    }
}
