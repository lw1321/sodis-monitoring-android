package de.sodis.monitoring.repository

import com.crashlytics.android.Crashlytics
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.AnswerJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.QuestionAnswer


class QuestionRepository(
    private val questionDao: QuestionDao,
    private val questionOptionDao: QuestionOptionDao,
    private val questionImageDao: QuestionImageDao,
    private val answerDao: AnswerDao,
    private val optionChoiceDao: OptionChoiceDao,
    private val monitoringApi: MonitoringApi
) {
    /**
     * include answers
     */
    fun getQuestionsBySurveySections(surveySectionIds: List<SurveySection>): MutableList<QuestionAnswer> {
        val questionList =
            questionDao.getBySurveySections(surveySectionIds.map { sectionItem -> sectionItem.id })
        val questionAnswerList: MutableList<QuestionAnswer> = mutableListOf()
        for (question: Question in questionList) {
            val questionOptionChoiceList: MutableList<QuestionOptionChoice> = mutableListOf()
            val questionOptions = questionOptionDao.getQuestionOptionsByQuestion(question.id)
            for (questionOption: QuestionOption in questionOptions) {
                //get the optionchoice..todo clean n:m query..
                val optionChoice = optionChoiceDao.getById(questionOption.optionChoiceId)
                questionOptionChoiceList.add(
                    QuestionOptionChoice(
                        questionOption = questionOption,
                        optionChoice = optionChoice
                    )
                )
            }
            val image = questionImageDao.getById(question.questionImageId)
            questionAnswerList.add(
                QuestionAnswer(
                    question = question,
                    answers = questionOptionChoiceList.toList(),
                    image = image,
                    title = surveySectionIds.first { surveySection -> surveySection.id == question.surveySectionId }.sectionTitle
                )
            )
        }
        return questionAnswerList
    }

    /**
     * Save questions in loval database, also try to upload them..Also save if the upload was successfully
     */
    fun saveQuestions(answerMap: MutableMap<Int, Answer>) {
        for ((k, v) in answerMap) {
            answerDao.insert(v)
        }
    }

    suspend fun uploadQuestions() {
        val allUnsubmitted = answerDao.getAllUnsubmitted().map { it.toAnswerJson() }
        try {
            monitoringApi.postAnswers(allUnsubmitted)
            answerDao.setSubmitted(allUnsubmitted.map { answer -> answer.id!! })
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }

    }
}

private fun Answer.toAnswerJson(): AnswerJson {
    return AnswerJson(
        answerNumeric = this.answerNumeric,
        answerYn = this.answerYn,
        answerText = this.answerText,
        interviewee = AnswerJson.Interviewee(this.intervieweeId),
        questionOption = AnswerJson.QuestionOption(this.questionOptionId),//apiseitig optional value
        timestamp = this.timeStamp
    )
}
