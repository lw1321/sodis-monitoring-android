package de.sodis.monitoring.repository

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import coil.Coil
import coil.api.get
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class SurveyRepository(
    private val surveyHeaderDao: SurveyHeaderDao,
    private val surveySectionDao: SurveySectionDao,
    private val questionDao: QuestionDao,
    private val inputTypeDao: InputTypeDao,
    private val questionImageDao: QuestionImageDao,
    private val questionOptionDao: QuestionOptionDao,
    private val optionChoiceDao: OptionChoiceDao,
    private val technologyDao: TechnologyDao,
    private val monitoringApi: MonitoringApi
) {

    /**
     * If internet connection is available, load all surveys and save it in the local database.
     * Also load and save the associated images in the internal storage.
     */
    suspend fun loadSurveys() {

        //TODO save the surveyheaderIds, surveySectionIds and questionIds, afterwards delete all other.
        val headerIds = mutableListOf<Int>()
        val sectionIds = mutableListOf<Int>()
        val questionIds = mutableListOf<Int>()

        val response = monitoringApi.getSurveys()
        //loop through surveys
        for (surveyHeaderJson: SurveyHeaderJson in response) {
            //save survey Header
            //Save Input Types

            if (technologyDao.count(surveyHeaderJson.technology.id) == 0) {
                //save input type
                technologyDao.insert(
                    Technology(
                        id = surveyHeaderJson.technology.id,
                        name = surveyHeaderJson.technology.name
                    )
                )
            }
            val header = SurveyHeader(
                id = surveyHeaderJson.id,
                surveyName = surveyHeaderJson.surveyName,
                technologyId = surveyHeaderJson.technology.id
            )
            if (surveyHeaderDao.exists(surveyHeaderJson.id) == 0) {
                surveyHeaderDao.insert(header)
            } else {
                surveyHeaderDao.update(header)
            }

            //add to temp lits
            headerIds.add(surveyHeaderJson.id)
            //loop through sections
            for (surveySectionJson: SurveyHeaderJson.SurveySectionJson in surveyHeaderJson.surveySection) {
                //save SurveySections
                surveySectionDao.insert(
                    SurveySection(
                        id = surveySectionJson.id,
                        sectionName = surveySectionJson.sectionName,
                        surveyHeaderId = surveyHeaderJson.id
                    )
                )
                //add to temp lits
                sectionIds.add(surveySectionJson.id)

                //loop through questions
                for (questionJson: SurveyHeaderJson.SurveySectionJson.QuestionJson in surveySectionJson.questions) {
                    //Save Input Types
                    if (inputTypeDao.exists(questionJson.inputType.id) == 0) {
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
                            dependentQuestionId = questionJson.dependentQuestionId,
                            dependentQuestionOptionId = questionJson.dependentQuestionOptionId,
                            inputTypeId = questionJson.inputType.id,
                            questionImageId = questionJson.questionImage?.id,
                            questionName = questionJson.questionName,
                            surveySectionId = surveySectionJson.id
                        )
                    )
                    //add to temp lits
                    questionIds.add(questionJson.id)

                    //Loop through question options
                    for (questionOptionJson: SurveyHeaderJson.SurveySectionJson.QuestionJson.QuestionOptionJson in questionJson.questionOptions) {
                        //check if option choice exist
                        if (optionChoiceDao.exists(questionOptionJson.optionChoice.id) == 0) {//TODO
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
                }
            }
        }
        //ok insert done. Now lets delete the outdated data
        surveyHeaderDao.deleteAllExcluded(headerIds)
        surveySectionDao.deleteAllExcluded(sectionIds)
        questionDao.deleteAllExcluded(questionIds)
    }

    /**
     * Provide a list of all in database stored survey headers.
     */
    fun getSurveyHeaders(): LiveData<List<SurveyHeader>> {
        return surveyHeaderDao.getAll()
    }

    /**
     * Provide a list of all in database stored survey headers.
     */
    fun getSurveyHeadersFilteredTechnology(technologyId: Int): LiveData<List<SurveyHeader>> {
        return surveyHeaderDao.getAllFilteredTechnology(technologyId)
    }


}