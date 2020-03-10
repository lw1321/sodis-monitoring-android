package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.IntervieweeJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.IntervieweeTechnology
import de.sodis.monitoring.db.entity.Technology
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.db.response.IntervieweeDetail

class IntervieweeRepository(
    private val intervieweeDao: IntervieweeDao,
    private val villageDao: VillageDao,
    private val technologyDao: TechnologyDao,
    private val intervieweeTechnologyDao: IntervieweeTechnologyDao,
    private val taskDao: TaskDao,
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
                    villageId = interviewee.village.id,
                    boysCount = interviewee.boysCount,
                    girlsCount = interviewee.girlsCount,
                    hasKnowledge = interviewee.hasKnowledge,
                    menCount = interviewee.menCount,
                    womenCount = interviewee.womenCount,
                    oldMenCount = interviewee.oldMenCount,
                    oldWomenCount = interviewee.oldWomenCount,
                    youngMenCount = interviewee.youngMenCount,
                    youngWomenCount = interviewee.youngWomenCount
                )
            )
            interviewee.intervieweeTechnologies.forEach {
                if (technologyDao.count(it.technology.id) == 0) {
                    //save input type
                    technologyDao.insert(
                        Technology(
                            id = it.technology.id,
                            name = it.technology.name
                        )
                    )
                }
                intervieweeTechnologyDao.insert(
                    IntervieweeTechnology(
                        id = it.id,
                        stateKnowledge = it.stateKnowledge,
                        technologyId = it.technology.id,
                        stateTechnology = it.stateTechnology,
                        intervieweeId = interviewee.id
                    )
                )
            }

        }
    }

    /*
    just basic info
     */
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

    /**
     * Full infos
     * interviewee, technologies, village
     */
    suspend fun getById(intervieweeId: Int): IntervieweeDetail {
        val intervieweeTechnologies =
            intervieweeTechnologyDao.getByInterviewee(intervieweeId)
        val interviewee = intervieweeDao.getById(intervieweeId)
        val village = villageDao.getById(interviewee.villageId)
        val taskList = taskDao.getTasksByInterviewee(intervieweeId)
        return IntervieweeDetail(
            interviewee = interviewee,
            intervieweeTechnologies = intervieweeTechnologies,
            village = village,
            tasks = taskList
        )
    }
}
