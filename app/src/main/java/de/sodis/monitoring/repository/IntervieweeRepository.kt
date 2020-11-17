package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.IntervieweeJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.IntervieweeDetail
import java.util.*

class IntervieweeRepository(
    private val intervieweeDao: IntervieweeDao,
    private val villageDao: VillageDao,
    private val technologyDao: TechnologyDao,
    private val intervieweeTechnologyDao: IntervieweeTechnologyDao,
    private val userDao: UserDao,
    private val monitoringApi: MonitoringApi
) {


    suspend fun loadAll() {
        //let's request a new list of interviewees to be sure our local data is up to data.
        //load villages
        val villageResponse = monitoringApi.getAllVillages()
        villageResponse.forEach {
            villageDao.insert(it)
        }

        val respo = monitoringApi.getInterviewees()

        for (interviewee: IntervieweeJson in respo) {
            //insert interviewee
            val intervieweeEntity = Interviewee(
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
                imagePath = null,//todo add attributes server side
                imageUrl = null//todo save image from url local
            )

            if (intervieweeDao.exists(interviewee.id) == 0) {
                intervieweeDao.insert(intervieweeEntity)
            } else {
                intervieweeDao.update(intervieweeEntity)
            }

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

    fun getIntervieweeByID(intervieweeId: String): Interviewee {
        return intervieweeDao.getById(intervieweeId)
    }

    /**
     * Full infos
     * interviewee, technologies, village
     */
    suspend fun getById(intervieweeId: String): IntervieweeDetail {
        val intervieweeTechnologies =
            intervieweeTechnologyDao.getByInterviewee(intervieweeId)
        val interviewee = intervieweeDao.getById(intervieweeId)
        val village = villageDao.getById(interviewee.villageId)
        var localExpert: User? = null
        if (interviewee.userId != null) {
            localExpert = userDao.getByLocalExpertId(interviewee.userId)
        }
        return IntervieweeDetail(
            interviewee = interviewee,
            intervieweeTechnologies = intervieweeTechnologies,
            village = village,
            user = localExpert
        )
    }

    fun getFamilyCount(intervieweeId: String): LiveData<Int> {
        return intervieweeDao.getFamilyCount(intervieweeId)
    }

    fun saveInterviewee(interviewee: Interviewee) {
        intervieweeDao.insert(interviewee)
    }

    fun getTechnologies(intervieweeId: String): LiveData<List<IntervieweeTechnology>> {
        return intervieweeTechnologyDao.getByIntervieweeLive(intervieweeId)
    }

    fun updateImagePath(id: String, currentPhotoPath: String) {
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

    suspend fun postInterviewee() {
        intervieweeDao.getAllNotSynced()
    }

    fun createInterviewee(name: String, village: Int) {
        val uniqueId: String = UUID.randomUUID().toString()

        val newInterviewee = Interviewee(
            id = uniqueId,
            name = name,
            villageId = village,
            boysCount = 0,
            menCount = 0,
            womenCount = 0,
            girlsCount = 0,
            oldMenCount = 0,
            oldWomenCount = 0,
            youngMenCount = 0,
            youngWomenCount = 0,
            userId = null,
            imagePath = null,
            imageUrl = null,
            synced = true
        )
        intervieweeDao.insert(newInterviewee)
        //create interviewee technologies
        val techno = technologyDao.getAllSync()
        techno.filter { it.name != "Dato Generales" }.forEach {
            intervieweeTechnologyDao.insert(
                IntervieweeTechnology(
                    id = UUID.randomUUID().toString(),
                    intervieweeId = newInterviewee.id,
                    technologyId = it.id,
                    stateKnowledge = 0,
                    stateTechnology = 0
                )
            )
        }
    }
}
