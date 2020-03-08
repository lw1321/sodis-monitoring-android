package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.SurveyRepository

class IntervieweeModel(application: Application) : AndroidViewModel(application) {

    var intervieweeList: LiveData<List<Interviewee>>
    var villageList: LiveData<List<Village>>


    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    private val intervieweeRepository =
        IntervieweeRepository(
            intervieweeDao = monitoringDatabase.intervieweeDao(),
            villageDao = monitoringDatabase.villageDao(),
            monitoringApi = MonitoringApi()
        )

    init {
        intervieweeList = intervieweeRepository.getAll()
        villageList = intervieweeRepository.getAllVillages()
    }

    fun getByVillage(villageId: Int): LiveData<List<Interviewee>> {
        return intervieweeRepository.getByVillage(villageId)
    }

    fun getInterviewee(intervieweeId: Int):LiveData<Interviewee>  {
        return intervieweeRepository.getById(intervieweeId)
    }
}
