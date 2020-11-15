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

    fun getCompletedSurvey(completedSurveyId: Int): List<CompletedSurveyDetail> {
        val completedQuestionDetailList: MutableList<CompletedSurveyDetail> = mutableListOf()
        //get the title of the completed survey

        //get completed Survey
        val completedSurvey = completedSurveyDao.getById(completedSurveyId)
        val surveyHeaderResponse = surveyHeaderDao.getByIdSync(completedSurvey.surveyHeaderId)
        val surveySectionList = surveyHeaderResponse!!.surveySectionList
        val answerList = answerDao.getAnswersByCompletedSurveyId(completedSurveyId)
        answerList.forEach { answer ->
            // get the depending question for the answer
            val question = questionDao.getByQuestionOptionId(answer.questionOptionId)
            //get the depending image for the question
            val image = question.questionImageId?.let { imageDao.getById(it) }
            completedQuestionDetailList.add(
                CompletedSurveyDetail(
                    question = question,
                    image = image ?: null,
                    title = surveySectionList.first {
                        it.id == question.surveySectionId
                    }.sectionName!!,
                    answer = answer
                )
            )
        }
        return completedQuestionDetailList
    }
}