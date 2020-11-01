package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.IntervieweeJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.IntervieweeDetail

class IntervieweeRepository(
    private val intervieweeDao: IntervieweeDao,
    private val villageDao: VillageDao,
    private val sectorDao: SectorDao,
    private val technologyDao: TechnologyDao,
    private val intervieweeTechnologyDao: IntervieweeTechnologyDao,
    private val taskDao: TaskDao,
    private val userDao: UserDao,
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

            if (interviewee.sector != null) {

                if (sectorDao.count(interviewee.sector.id) == 0) {
                    sectorDao.insert(
                        Sector(
                            id = interviewee.sector.id,
                            name = interviewee.sector.name,
                            villageId = interviewee.sector.village.id
                        )
                    )
                }
            }

            //insert interviewee
            intervieweeDao.insert(
                Interviewee(
                    id = interviewee.id,
                    name = interviewee.name,
                    villageId = interviewee.village.id,
                    girlsCount = interviewee.girlsCount,
                    boysCount = interviewee.boysCount,
                    youngMenCount = interviewee.youngMenCount,
                    youngWomenCount = interviewee.youngWomenCount,
                    oldMenCount = interviewee.oldMenCount,
                    oldWomenCount = interviewee.oldWomenCount,
                    menCount = interviewee.menCount,
                    womenCount = interviewee.womenCount,
                    userId = interviewee.user?.id,
                    sectorId = interviewee.sector?.id,
                    imagePath = null,//todo add attributes server side
                    imageUrl = null
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

    fun getVillageByID(id: Int): Village {
        return villageDao.getById(id)
    }

    fun getByVillage(villageId: Int): LiveData<List<Interviewee>> {
        return intervieweeDao.getByVillage(villageId)
    }

    fun searchByName(name: String): List<Interviewee> {
        return intervieweeDao.searchByName(name)
    }

    fun getIntervieweeByID(intervieweeId: Int): Interviewee {
        return intervieweeDao.getById(intervieweeId)
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
        val sector = interviewee.sectorId?.let { sectorDao.getById(it) }
        var localExpert: User? = null
        if (interviewee.userId != null) {
            localExpert = userDao.getByLocalExpertId(interviewee.userId)
        }
        val taskList = taskDao.getTasksByInterviewee(intervieweeId)
        return IntervieweeDetail(
            interviewee = interviewee,
            intervieweeTechnologies = intervieweeTechnologies,
            village = village,
            sector = sector,
            user = localExpert,
            tasks = taskList
        )
    }

    fun saveInterviewee(interviewee: Interviewee) {
        intervieweeDao.insert(interviewee)
    }

    fun getSectorsOfVillage(villageId: Int): LiveData<List<Sector>> {
        return sectorDao.getByVillageId(villageId)

    }

    fun getTechnologies(intervieweeId: Int): LiveData<List<IntervieweeTechnology>> {
        return intervieweeTechnologyDao.getByIntervieweeLive(intervieweeId)
    }

    fun updateImagePath(id: Int, currentPhotoPath: String) {
        val intervieweeByID = getIntervieweeByID(id)

        intervieweeByID.imagePath = currentPhotoPath
        intervieweeByID.synced = false
        intervieweeDao.update(intervieweeByID)
    }

    suspend fun uploadProfilPictures() {
        val allNotSynced = intervieweeDao.getAllNotSynced()
        allNotSynced.forEach { interviewee ->
            val postIntervieweImage = monitoringApi.postIntervieweImage(
                interviewee.imagePath,
                intervieweeId = interviewee.id
            )
            interviewee.imageUrl = postIntervieweImage.imageUrl
            interviewee.synced = true
            intervieweeDao.update(interviewee)
        }
    }
}
