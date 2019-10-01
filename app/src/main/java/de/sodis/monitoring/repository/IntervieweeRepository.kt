package de.sodis.monitoring.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.IntervieweeDao
import de.sodis.monitoring.db.entity.Interviewee

class IntervieweeRepository(private val intervieweeDao: IntervieweeDao, private val monitoringApi: MonitoringApi) {

    @WorkerThread
    suspend fun loadAll() {
        //let's request a new list of interviewees to be sure our local data is up to data.
        val intervieweesAsync = monitoringApi.getIntervieweesAsync()
        val resp = intervieweesAsync.await()
        for (interviewee: Interviewee in resp.body()!!){
            //insert interviewee
            intervieweeDao.insert(interviewee)
        }
    }

    fun getAll(): LiveData<List<Interviewee>> {
        return intervieweeDao.getAll()
    }

    fun getByName(name: String): LiveData<Interviewee>{
        return intervieweeDao.getByName(name)
    }
}
