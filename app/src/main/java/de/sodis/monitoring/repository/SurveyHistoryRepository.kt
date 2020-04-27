package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.CompletedSurvey
import de.sodis.monitoring.db.entity.Task
import de.sodis.monitoring.db.response.CompletedSurveyDetail
import de.sodis.monitoring.db.response.CompletedSurveyOverview
import de.sodis.monitoring.db.response.QuestionAnswer

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

    fun getCompletedSurvey(completedSurveyId: Int): List<CompletedSurveyDetail> {
        val completedQuestionDetailList: MutableList<CompletedSurveyDetail> = mutableListOf()
        //get the title of the completed survey

        //get completed Survey
        val completedSurvey = completedSurveyDao.getById(completedSurveyId)
        val surveyHeaderResponse = surveyHeaderDao.getByIdSync(completedSurvey.surveyHeaderId)
        val surveySectionList = surveyHeaderResponse!!.surveySectionList
        val answerList = answerDao.getAnswersByCompletedSurveyId(completedSurveyId)
        answerList.forEach {answer ->
            // get the depending question for the answer
            val question = questionDao.getByQuestionOptionId(answer.questionOptionId)
            //get the depending image for the question
            val image = imageDao.getById(question.questionImageId)
            completedQuestionDetailList.add(
                CompletedSurveyDetail(
                    question=question,
                    image = image,
                    title = surveySectionList.first {
                        it.id==question.surveySectionId
                    }.sectionName!!,
                    answer = answer
                )
            )
        }
        return completedQuestionDetailList
    }
}