package de.sodis.monitoring.repository

import android.content.Context
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.CompletedSurveyJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.QuestionAnswer
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import java.io.File
import java.util.*


class QuestionRepository(
        private val questionDao: QuestionDao,
        private val questionOptionDao: QuestionOptionDao,
        private val questionImageDao: QuestionImageDao,
        private val answerDao: AnswerDao,
        private val optionChoiceDao: OptionChoiceDao,
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
    //TODO REFACTORING
    suspend fun uploadQuestions() {
        // 1. get all completed surveys
        val tempCompletedSurveysList: MutableList<CompletedSurveyJson> = mutableListOf()
        val completedSurveys = completedSurveyDao.getAllUnsubmitted()
        completedSurveys.forEach {
            // 2. for each completed survey get all answers.
            val answers = answerDao.getAnswersByCompletedSurveyId(it.id!!).map { it.toAnswerJson() }// TODO ID GENERATION
            // 3. link answerlist to the completed survey
            val interviewee = intervieweeDao.getById(it.intervieweeId)

            val completedSurveyJson = CompletedSurveyJson(
                    id = it.id,
                    answers = answers,
                    interviewee = CompletedSurveyJson.Interviewee(id = interviewee.id, name = interviewee.name, village = CompletedSurveyJson.Interviewee.Village(id = interviewee.villageId)),
                    creationDate = it.timeStamp,
                    surveyHeader = CompletedSurveyJson.SurveyHeader(it.surveyHeaderId),
                    latitude = it.latitude,
                    longitude = it.longitude
            )
            // 4. add the combined completed survey to a temp list
            tempCompletedSurveysList.add(completedSurveyJson);
        }
        if (tempCompletedSurveysList.size != 0) {
            // 5. send temp list as body to POST completed-surveys
            monitoringApi.postCompletedSurveys(tempCompletedSurveysList)
            // 6. If successfull set submitted to true for all completed surveys.
            completedSurveyDao.setSubmitted(completedSurveys.map { completedSurvey -> completedSurvey.id!! })
        }
    }

    suspend fun uploadAnswerImages(applicationContext: Context) {
        //check for not uploaded images where the answers are already synced.
        val notSubmittedImages = answerDao.getNotSubmittedImages()
        //upload the images
        notSubmittedImages.forEach{ answer ->
            //Compress File
            val compressedImageFile = Compressor.compress(applicationContext, File(answer.imagePath!!))
            monitoringApi.postAnswerImage(answer.id, compressedImageFile)
        }
        //update the photo path
    }

}


private fun Answer.toAnswerJson(): CompletedSurveyJson.Answer {//TODO SET UUID on saving to local db!!
    return CompletedSurveyJson.Answer(
            id = this.id,
            questionOption = CompletedSurveyJson.Answer.QuestionOption(this.questionOptionId),
            answerText = this.answerText
    )
}


