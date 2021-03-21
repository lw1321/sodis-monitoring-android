package de.sodis.monitoring.repository

import android.content.Context
import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.repository.SurveyRepository.Companion.bitmapToFile


class SurveyRepository(
    private val surveyHeaderDao: SurveyHeaderDao,
    private val surveySectionDao: SurveySectionDao,
    private val questionDao: QuestionDao,
    private val inputTypeDao: InputTypeDao,
    private val optionChoiceDao: OptionChoiceDao,
    private val questionOptionDao: QuestionOptionDao,
    private val questionImageDao: QuestionImageDao,

    private val monitoringApi: MonitoringApi
) {

    /**
     * If internet connection is available, load all surveys and save it in the local database.
     * Also load and save the associated images in the internal storage.
     */
    suspend fun loadSurveys() {
        //TODO save
        // get survey headers
        val response = monitoringApi.getSurveys()
    }

    suspend fun loadSections() {
        val response = monitoringApi.getSections()
        //TODO
    }

    suspend fun loadQuestions() {
        monitoringApi.getOptionChoices()
        monitoringApi.getInputTypes()
        //TODO
        val questionImageList = monitoringApi.getQuestionImages()
        //loop through surveys
        for (questionImage: QuestionImage in questionImageList) {
            //load bitmap from url
            //TODO
            if (questionImageDao.exists(questionImage.id) == 0) {
                //udpate
                questionImageDao.update(questionImage)
            } else {
                questionImageDao.insert(questionImage)
            }
        }
    }

    /**
     * Provide a list of all in database stored survey headers.
     */
    fun getSurveyHeadersFilteredTechnology(technologyId: Int): LiveData<List<SurveyHeader>> {
        return surveyHeaderDao.getAllFilteredTechnology(technologyId)
    }

    fun getSurveyHeadersFilteredTechnologySynchronous(technologyID: Int): List<SurveyHeader> {
        return surveyHeaderDao.getAllFilteredTechnologySync(technologyID)
    }

    /**
     * If internet connection is available, load all question images
     */
    suspend fun storeImages(context: Context) {
        val allNotDownloadedList = questionImageDao.getAllNotDownloaded()
        //loop through surveys
        for (questionImage: QuestionImage in allNotDownloadedList) {
            //save survey Header
            //store
            val absolutePath = Utils.urlToFile(questionImage.url, context)
            questionImage.path = absolutePath
            //Save Images
            questionImageDao.update(
                questionImage
            )
        }
    }


}