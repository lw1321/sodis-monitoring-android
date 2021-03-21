package de.sodis.monitoring.repository

import android.content.Context
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.QuestionAnswer
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import java.io.File
import java.util.*


class QuestionRepository(
    private val answerDao: AnswerDao,
    private val completedSurveyDao: CompletedSurveyDao,
    private val monitoringApi: MonitoringApi,
    private val intervieweeDao: IntervieweeDao
) {


    /**
     * Save questions in loval database, also try to upload them..Also save if the upload was successfully
     */
    fun saveQuestions(
        answerMap: MutableMap<Int, Answer>,
        completedSurvey: CompletedSurvey
    ) {
        completedSurveyDao.insert(completedSurvey)

        for ((_, v) in answerMap) {
            v.completedSurveyId = completedSurvey.id
            answerDao.insert(v)
        }
    }

    suspend fun syncCompletedSurveys() {
        // TODO implement after server endpoints are adjust
        monitoringApi.postCompletedSurveys(completedSurveyDao.getAllUnsubmitted())
        //upload answers
        //upload images
    }

    suspend fun uploadSurveyImages(applicationContext: Context) {
        //check for not uploaded images where the answers are already synced.
        val notSubmittedImages = answerDao.getNotSubmittedImages()
        //upload the images
        notSubmittedImages.forEach { answer ->
            //Compress File
            val compressedImageFile =
                Compressor.compress(applicationContext, File(answer.imagePath!!))
            monitoringApi.postAnswerImage(answer.id, compressedImageFile)
        }
        //update the photo path
    }

}



