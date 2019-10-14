package de.sodis.monitoring.repository

import de.sodis.monitoring.db.dao.QuestionDao
import de.sodis.monitoring.db.dao.QuestionImageDao
import de.sodis.monitoring.db.dao.QuestionOptionDao
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.response.QuestionAnswer


class QuestionRepository(
    private val questionDao: QuestionDao,
    private val questionOptionDao: QuestionOptionDao,
    private val questionImageDao: QuestionImageDao
) {
    /**
     * include answers
     */
    fun getQuestionsBySurveySections(surveySectionIds: List<Int>): MutableList<QuestionAnswer> {
        val questionList = questionDao.getBySurveySections(surveySectionIds)
        val questionAnswerList: MutableList<QuestionAnswer> = mutableListOf()
        for (question: Question in questionList.value!!){
            var questionOptions = questionOptionDao.getByQuestion(question.id)
            var image = questionImageDao.getById(question.questionImageId)
            questionAnswerList.add(
                QuestionAnswer(
                    question=question,
                    answers = questionOptions,
                    image = image
                )
            )
        }
        return questionAnswerList
    }
}