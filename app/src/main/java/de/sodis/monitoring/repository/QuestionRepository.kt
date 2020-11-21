package de.sodis.monitoring.repository

import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.AnswerJson
import de.sodis.monitoring.api.model.CompletedSurveyJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.QuestionAnswer
import java.util.*


class QuestionRepository(
    private val questionDao: QuestionDao,
    private val questionOptionDao: QuestionOptionDao,
    private val questionImageDao: QuestionImageDao,
    private val answerDao: AnswerDao,
    private val optionChoiceDao: OptionChoiceDao,
    private val completedSurveyDao: CompletedSurveyDao,
    private val monitoringApi: MonitoringApi,
    private val intervieweeTechnologyDao: IntervieweeTechnologyDao,
    private val intervieweeDao: IntervieweeDao,
    private val surveyHeaderDao: SurveyHeaderDao
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
            val image = question.questionImageId?.let { questionImageDao.getById(it) }
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
        /**
         * Statusnerechnung:
         * 0 = Keine Infos(Grau)
        1 = Etwas passt nicht (Farbe rot) (Mindestestens eine Frage wurde nicht mit Si/lachendem Smiiley beantwortet.
        2 = Alles Super (Grün) (Alle Fragen positiv beantwortet)
         */
        //intervieweetechnology holen
        val surveyHeader =
            surveyHeaderDao.getByIdSync(surveyHeaderId = completedSurvey.surveyHeaderId)
        val intervieweeTechnology = intervieweeTechnologyDao.getByIntervieweeAndTechnoology(
            completedSurvey.intervieweeId,
            surveyHeader.surveyHeader.technologyId
        )

        if (surveyHeader.surveyHeader.surveyName == "Miembros de la familia") {//TODO
            //update the family infos
            val interviewee = intervieweeDao.getById(completedSurvey.intervieweeId)
             completedSurveyDao.insert(completedSurvey)
            var ageSum = 0
            for ((k, v) in answerMap) {//todo insertAll
                v.completedSurveyId = completedSurvey.id
                ageSum += v.answerText!!.toInt()
                answerDao.insert(v)
            }
            interviewee.menCount = ageSum//todo just one attribute members remove others
            intervieweeDao.update(interviewee)
        } else {
            //status berechnen
            var status = 2
            val completedSurveyId = completedSurveyDao.insert(completedSurvey)
            for ((k, v) in answerMap) {//todo insertAll
                v.completedSurveyId = completedSurvey.id
                answerDao.insert(v)
                if (!v.answerText.equals("Si"))//TODO{
                //something is not "postiv" so lets set the status to 1
                    status = 1
            }
            if (intervieweeTechnology != null) {
                if (surveyHeader.surveyHeader.surveyName.toLowerCase(Locale.getDefault())
                        .contains("practicas")
                ) {
                    intervieweeTechnology.stateKnowledge = status
                } else {
                    intervieweeTechnology.stateTechnology = status
                }
                //status speichern
                intervieweeTechnologyDao.update(intervieweeTechnology)
            }
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
            val interviewee = intervieweeDao.getById(it.intervieweeId)

            val completedSurveyJson = CompletedSurveyJson(
                id = it.id,
                answers = answers,
                interviewee = CompletedSurveyJson.Interviewee(id = interviewee.id, name = interviewee.name, village = CompletedSurveyJson.Interviewee.Village(id=interviewee.villageId)),
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
            //todo "mapping" local and server ids
            val postCompletedSurveys = monitoringApi.postCompletedSurveys(tempCompletedSurveysList)
            // 6. If successfull set submitted to true for all completed surveys.
            completedSurveyDao.setSubmitted(completedSurveys.map { completedSurvey -> completedSurvey.id!! })
        }

    }
}

private fun Answer.toAnswerJson(): CompletedSurveyJson.Answer {
    return CompletedSurveyJson.Answer(
        id = UUID.randomUUID().toString(),
        answerYn = this.answerYn,
        questionOption = CompletedSurveyJson.Answer.QuestionOption(this.questionOptionId),
        answerText = this.answerText
    )
}


