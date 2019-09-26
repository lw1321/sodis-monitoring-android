package de.sodis.monitoring.repository

import androidx.annotation.WorkerThread
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*

class SurveyRepository(
    private val surveyHeaderDao: SurveyHeaderDao,
    private val surveySectionDao: SurveySectionDao,
    private val questionDao: QuestionDao,
    private val inputTypeDao: InputTypeDao,
    private val questionImageDao: QuestionImageDao,
    private val questionOptionDao: QuestionOptionDao,
    private val optionChoiceDao: OptionChoiceDao,
    private val monitoringApi: MonitoringApi
) {
    /**
     * If internet connection is available, load all surveys and save it in the local database.
     * Also load and save the associated images in the internal storage.
     */
    @WorkerThread
    suspend fun loadSurveys() {
        try {
            val getAllSurveysRequest = monitoringApi.getSurveysAsync()
            val response = getAllSurveysRequest.await()
            //loop through surveys
            for (surveyHeaderJson: SurveyHeaderJson in response.body()!!) {
                //save survey Header
                surveyHeaderDao.insert(
                    SurveyHeader(
                        id = surveyHeaderJson.id,
                        surveyName = surveyHeaderJson.surveyName,
                        instructions = surveyHeaderJson.instructions,
                        otherHeaderInfo = surveyHeaderJson.otherHeaderInfo
                    )
                )

                //loop through sections
                for (surveySectionJson: SurveyHeaderJson.SurveySectionJson in surveyHeaderJson.surveySection){
                    //save SurveySections
                    surveySectionDao.insert(
                        SurveySection(
                            id = surveySectionJson.id,
                            sectionName = surveySectionJson.sectionName,
                            sectionSubheading = surveySectionJson.sectionSubheading,
                            sectionTitle =  surveySectionJson.sectionTitle,
                            surveyHeaderId = surveyHeaderJson.id
                        )
                    )
                    //loop through questions
                    for(questionJson: SurveyHeaderJson.SurveySectionJson.QuestionJson in surveySectionJson.questions){

                        //check if image exists. Room returns count, less memory than query the object.
                        if(questionImageDao.exists(questionJson.questionImage.id) == 0){
                            // TODO: save image to internal storage, store path in database
                            //Save Images
                            questionImageDao.insert(
                                QuestionImage(
                                    id = questionJson.questionImage.id,
                                    url = questionJson.questionImage.url,
                                    path = null
                                )
                            )
                        }
                        //Loop through question options
                        for(questionOptionJson: SurveyHeaderJson.SurveySectionJson.QuestionJson.QuestionOptionJson in questionJson.questionOptions){
                            //check if option choice exist
                            if(optionChoiceDao.exists(questionOptionJson.optionChoice.id) == 0){
                                optionChoiceDao.insert(
                                    OptionChoice(
                                        id = questionOptionJson.optionChoice.id,
                                        optionChoiceName = questionOptionJson.optionChoice.optionChoiceName
                                    )
                                )
                            }
                            //save question option, many to many relationship between question and OptionCoice.
                            questionOptionDao.insert(
                                QuestionOption(
                                    id = questionOptionJson.id,
                                    optionChoiceId = questionOptionJson.optionChoice.id,
                                    questionId = questionJson.id
                                )
                            )
                        }
                        //Save Input Types
                        if(inputTypeDao.exists(questionJson.inputType.id) == 0){
                            //save input type
                            inputTypeDao.insert(
                                InputType(
                                    id = questionJson.inputType.id,
                                    inputTypeName = questionJson.inputType.inputTypeName
                                )
                            )
                        }
                        //Save Questions
                        questionDao.insert(
                            Question(
                                id = questionJson.id,
                                dependentQuestionId = questionJson.dependentQuestionid,
                                dependentQuestionOptionId = questionJson.dependentQuestionOptionid,
                                inputTypeId = questionJson.inputType.id,
                                questionImageId = questionJson.questionImage.id,
                                questionName = questionJson.questionName,
                                questionSubtext = questionJson.questionSubtext,
                                surveySectionId = surveySectionJson.id
                            )
                        )
                    }
                }
            }
        } catch (e: Throwable) {
            print(e.localizedMessage)
        }
    }
}