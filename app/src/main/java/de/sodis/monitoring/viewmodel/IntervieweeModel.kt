package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.db.response.IntervieweeDetail
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.SurveyRepository
import androidx.lifecycle.MutableLiveData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.lifecycle.viewModelScope
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
}
