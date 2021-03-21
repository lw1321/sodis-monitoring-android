package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.response.CompletedSurveyDetail
import de.sodis.monitoring.db.response.CompletedSurveyOverview

class SurveyHistoryRepository(
    private val completedSurveyDao: CompletedSurveyDao,
    private val questionDao: QuestionDao,
    private val imageDao: QuestionImageDao,
    private val surveyHeaderDao: SurveyHeaderDao,
    private val answerDao: AnswerDao
) {


    fun getCompletedSurveys(): LiveData<List<CompletedSurveyOverview>> {
        return completedSurveyDao.getAll()
    }

    fun getCompletedSurveysSorted():LiveData<List<CompletedSurveyOverview>> {
        return completedSurveyDao.getAllSorted()
    }

    fun getCompletedSurvey(completedSurveyId: String): List<CompletedSurveyDetail> {
        return completedSurveyDao.getAnswers(completedSurveyId)
    }
}