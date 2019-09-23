package de.sodis.monitoring.repository

import androidx.annotation.WorkerThread
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.MonitoringApiInterface
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.SurveyHeader

class SurveyRepository (
    private val surveyHeaderDao: SurveyHeaderDao,
    private val surveySectionDao: SurveySectionDao,
    private val questionDao: QuestionDao,
    private val inputTypeDao: InputTypeDao,
    private val questionImageDao: QuestionImageDao,
    private val questionOptionDao: QuestionOptionDao,
    private val optionChoiceDao: OptionChoiceDao,
    private val monitoringApi: MonitoringApi
){
    /**
     * If internet connection is available, load all surveys and save it in the local database.
     * Also load and save the associated images in the internal storage.
     */
    @WorkerThread
    suspend fun loadSurveys (){
        try {
            val getAllSurveysRequest = monitoringApi.getSurveys()
            val response = getAllSurveysRequest.await()
            //loop through surveys
            for (surveyHeader: SurveyHeader in response.body()!!){
                //save survey Header
                surveyHeaderDao.insert(surveyHeader)
            }
        }
        catch (e: Throwable){
            print(e.localizedMessage)
        }
    }
}