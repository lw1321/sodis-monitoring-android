package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.models.IntervieweeJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.FamilyList
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
                )
            )
        }
    }


    fun getFamilyList(): LiveData<List<FamilyList>> {
        return intervieweeDao.getFamilyList()
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
            monitoringApi.postInterviewee(
                IntervieweeJson(
                    id = interviewee.id,
                    name = interviewee.name,
                    imageUrl = interviewee.imageUrl,
                    village = IntervieweeJson.Village(
                        id = interviewee.villageId,
                        name = null
                    )
                )
            )
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

    fun storeIntervieweeImagePath(currentPhotoPath: String, intervieweeId: String) {
        val intervieweeOld = intervieweeDao.getById(intervieweeId)
        intervieweeOld.imagePath = currentPhotoPath
        intervieweeOld.imageUrl = null // set null because image is not uploaded yet
        intervieweeDao.update(intervieweeOld)
    }
}
