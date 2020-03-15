package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.db.response.IntervieweeDetail
import de.sodis.monitoring.repository.IntervieweeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class IntervieweeModel(application: Application) : AndroidViewModel(application) {

    var intervieweeList: LiveData<List<Interviewee>>
    var villageList: LiveData<List<Village>>
    var intervieweeDetail: MutableLiveData<IntervieweeDetail>

    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    private val intervieweeRepository =
        IntervieweeRepository(
            intervieweeDao = monitoringDatabase.intervieweeDao(),
            villageDao = monitoringDatabase.villageDao(),
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

     fun updateInterviewee(interviewee: Interviewee) {
        viewModelScope.launch(Dispatchers.Default) {
            intervieweeRepository.updateInterviewee(interviewee)
        }
    }
}
