package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.IntervieweeDetail
import java.util.*

class PlaceRepository(
    private val intervieweeDao: IntervieweeDao,
    private val villageDao: VillageDao,
    private val userDao: UserDao,
    private val monitoringApi: MonitoringApi
) {

    suspend fun loadVillages() {
        val villageResponse = monitoringApi.getAllVillages()
        villageResponse.forEach {
            villageDao.insert(it)
        }
    }

    suspend fun loadFamilies() {
        val interviewees = monitoringApi.getInterviewees()
        interviewees.forEach { interviewee ->
            intervieweeDao.insert(
                Interviewee(
                id = interviewee.id,
                villageId = interviewee.village.id,
                    imagePath = null,
                    imageUrl = interviewee.imageUrl,
                    name = interviewee.name,
                    synced = true,
                    userId = null
            ))
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

    suspend fun syncProfilPictures() {
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

    suspend fun syncInterviewee() {
        val notSyncedInterviewee = intervieweeDao.getAllNotSynced()
        notSyncedInterviewee.forEach { interviewee ->
            //post interviewee
            val postInterviewee = monitoringApi.postInterviewee(interviewee)
            interviewee.synced = true
            intervieweeDao.update(interviewee = interviewee)
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
}
