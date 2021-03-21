package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.CompletedSurveyJson
import de.sodis.monitoring.api.model.IntervieweeJson
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.IntervieweeDetail
import java.util.*

class IntervieweeRepository(
        private val intervieweeDao: IntervieweeDao,
        private val villageDao: VillageDao,
        private val technologyDao: TechnologyDao,
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
                    userId = null,
                    imagePath = null,//todo add attributes server side
                    imageUrl = null//todo save image from url local
            )

            if (intervieweeDao.exists(interviewee.id) == 0) {
                intervieweeDao.insert(intervieweeEntity)
            } else {
                intervieweeDao.update(intervieweeEntity)
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
        val interviewee = intervieweeDao.getById(intervieweeId)
        val village = villageDao.getById(interviewee.villageId)
        var localExpert: User? = null
        if (interviewee.userId != null) {
            localExpert = userDao.getByLocalExpertId(interviewee.userId)
        }
        return IntervieweeDetail(
                interviewee = interviewee,
                village = village,
                user = localExpert
        )
    }
    fun saveInterviewee(interviewee: Interviewee) {
        intervieweeDao.insert(interviewee)
    }

    fun updateImagePath(id: String, currentPhotoPath: String) {
        val intervieweeByID = getIntervieweeByID(id)

        intervieweeByID.imagePath = currentPhotoPath
        intervieweeByID.synced = false
        intervieweeDao.update(intervieweeByID)
    }

    suspend fun uploadProfilPictures() {
        val allNotSynced = intervieweeDao.getNotsyncedProfilePictures()
        allNotSynced.forEach { interviewee ->
            val postIntervieweImage = monitoringApi.postIntervieweImage(
                    interviewee.imagePath,
                    intervieweeId = interviewee.id
            )
            interviewee.imageUrl = postIntervieweImage.imageUrl
            intervieweeDao.update(interviewee)
        }
    }

    suspend fun postIntervieweeAndTechnology() {
        val notSyncedInterviewee = intervieweeDao.getAllNotSynced()
        notSyncedInterviewee.forEach {
            //post interviewee
            val postInterviewee = monitoringApi.postInterviewee(CompletedSurveyJson.Interviewee(
                    id = it.id,
                    village = CompletedSurveyJson.Interviewee.Village(
                            id = it.villageId
                    ),
                    name = it.name
            ))
            it.synced = true
            intervieweeDao.update(interviewee = it)
        }
    }

    fun createInterviewee(name: String, village: Int) {
        val uniqueId: String = UUID.randomUUID().toString()
        val newInterviewee = Interviewee(
                id = uniqueId,
                name = name,
                villageId = village,
                userId = null,
                imagePath = null,
                imageUrl = null,
                synced = false
        )
        intervieweeDao.insert(newInterviewee)
    }

    fun getVillageName(id: Int): String {
        return villageDao.getNameById(id)
    }
}
