package de.sodis.monitoring.repository

import android.os.Build
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.AnswerJson
import de.sodis.monitoring.db.dao.AnswerDao
import de.sodis.monitoring.db.dao.QuestionDao
import de.sodis.monitoring.db.dao.QuestionImageDao
import de.sodis.monitoring.db.dao.QuestionOptionDao
import de.sodis.monitoring.db.entity.Answer
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.Question
import de.sodis.monitoring.db.response.QuestionAnswer


class QuestionRepository(
    private val questionDao: QuestionDao,
    private val questionOptionDao: QuestionOptionDao,
    private val questionImageDao: QuestionImageDao,
    private val answerDao: AnswerDao,
    private val monitoringApi: MonitoringApi
) {
    /**
     * include answers
     */
    fun getQuestionsBySurveySections(title: String, surveySectionIds: List<Int>): MutableList<QuestionAnswer> {
        val questionList = questionDao.getBySurveySections(surveySectionIds)
        val questionAnswerList: MutableList<QuestionAnswer> = mutableListOf()
        for (question: Question in questionList){
            var questionOptions = questionOptionDao.getByQuestion(question.id)
            var image = questionImageDao.getById(question.questionImageId)
            questionAnswerList.add(
                QuestionAnswer(
                    question=question,
                    answers = questionOptions,
                    image = image,
                    title = title
                )
            )
        }
        return questionAnswerList
    }

    /**
     * Save questions in loval database, also try to upload them..Also save if the upload was successfully
     */
    fun saveQuestions(answerMap: MutableMap<Int, Answer>) {
        for((k,v) in answerMap){
            answerDao.insert(v)
        }
    }

    fun uploadQuestions(){
        val allUnsubmitted = answerDao.getAllUnsubmitted()
        val tempList = mutableListOf<AnswerJson>()
        allUnsubmitted.forEach { it ->
            tempList.add(
                it.toAnswerJson()
            )
        }
        monitoringApi.postAnswers(tempList)
    }

}

private fun Answer.toAnswerJson(): AnswerJson {
    return AnswerJson(
        answerNumeric = this.answerNumeric,
        answerYn = this.answerYn,
        answerText = this.answerText,
        interviewee = AnswerJson.Interviewee(this.intervieweeId),
        questionOption = AnswerJson.QuestionOption(this.questionOptionId)//apiseitig optional value
    )
}
