package de.sodis.monitoring.repository

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.AnswerJson
import de.sodis.monitoring.api.model.CompletedSurveyJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.QuestionAnswer


class QuestionRepository(
    private val questionDao: QuestionDao,
    private val questionOptionDao: QuestionOptionDao,
    private val questionImageDao: QuestionImageDao,
    private val answerDao: AnswerDao,
    private val optionChoiceDao: OptionChoiceDao,
    private val completedSurveyDao: CompletedSurveyDao,
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
                    title = surveySectionIds.first { surveySection -> surveySection.id == question.surveySectionId }.sectionName
                )
            )
        }
        return questionAnswerList
    }

    /**
     * Save questions in loval database, also try to upload them..Also save if the upload was successfully
     */

    fun saveQuestions(
        answerMap: MutableMap<Int, Answer>,
        completedSurvey: CompletedSurvey
    ) {
        val completedSurveyId = completedSurveyDao.insert(completedSurvey)
        for ((k, v) in answerMap) {//todo insertAll
            v.completedSurveyId = completedSurveyId.toInt()
            answerDao.insert(v)
        }
    }

    suspend fun uploadQuestions() {
        // 1. get all completed surveys
        val tempCompletedSurveysList: MutableList<CompletedSurveyJson> = mutableListOf()
        val completedSurveys = completedSurveyDao.getAllUnsubmitted()
        completedSurveys.forEach {
            // 2. for each completed survey get all answers.
            val answers = answerDao.getAnswersByCompletedSurveyId(it.id!!).map { it.toAnswerJson() }
            // 3. link answerlist to the completed survey
            val completedSurveyJson = CompletedSurveyJson(
                answers = answers,
                interviewee = CompletedSurveyJson.Interviewee(id = it.id),
                creationDate = it.timeStamp,
                surveyHeader = CompletedSurveyJson.SurveyHeader(it.surveyHeaderId),
                latitude = it.latitude,
                longitude = it.longitude!!
            )
            // 4. add the combined completed survey to a temp list
            tempCompletedSurveysList.add(completedSurveyJson);
        }
        if (tempCompletedSurveysList.size != 0) {
            // 5. send temp list as body to POST completed-surveys
            //todo "mapping" local and server ids
            val postCompletedSurveys = monitoringApi.postCompletedSurveys(tempCompletedSurveysList)
            // 6. If successfull set submitted to true for all completed surveys.
            completedSurveyDao.setSubmitted(completedSurveys.map { completedSurvey -> completedSurvey.id!! })
        }

    }
}

private fun Answer.toAnswerJson(): CompletedSurveyJson.Answer {
    return CompletedSurveyJson.Answer(
        answerYn = this.answerYn,
        questionOption = CompletedSurveyJson.Answer.QuestionOption(this.questionOptionId),
        answerText = this.answerText
    )
}


