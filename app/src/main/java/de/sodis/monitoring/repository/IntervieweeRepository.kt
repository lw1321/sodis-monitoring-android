package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.IntervieweeJson
import de.sodis.monitoring.db.dao.AnswerDao
import de.sodis.monitoring.db.dao.IntervieweeDao
import de.sodis.monitoring.db.dao.VillageDao
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.Village

class IntervieweeRepository(
    private val intervieweeDao: IntervieweeDao,
    private val villageDao: VillageDao,
    private val monitoringApi: MonitoringApi
) {


    suspend fun loadAll() {
        //let's request a new list of interviewees to be sure our local data is up to data.
        val respo = monitoringApi.getInterviewees()

        for (interviewee: IntervieweeJson in respo) {
            //add village
            if (villageDao.count(interviewee.village.id) == 0) {
                villageDao.insert(
                    Village(
                        id = interviewee.village.id, name = interviewee.village.name
                    )
                )
            }
            //insert interviewee
            intervieweeDao.insert(
                Interviewee(
                    id = interviewee.id,
                    name = interviewee.name,
                    villageId = interviewee.village.id
                )
            )
        }
    }

    fun getAll(): LiveData<List<Interviewee>> {
        return intervieweeDao.getAll()
    }

    fun getByName(name: String): LiveData<Interviewee> {
        return intervieweeDao.getByName(name)
    }

    fun getAllVillages(): LiveData<List<Village>> {
        return villageDao.getAll()
    }

    fun getByVillage(villageId: Int): LiveData<List<Interviewee>> {
        return intervieweeDao.getByVillage(villageId)
    }
}
