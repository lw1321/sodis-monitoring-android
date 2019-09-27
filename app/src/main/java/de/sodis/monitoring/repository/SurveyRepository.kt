package de.sodis.monitoring.repository

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
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
    private val monitoringApi: MonitoringApi
) {


    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap:Bitmap, context: Context): String? {
        // Get the context wrapper
        val wrapper = ContextWrapper(context.applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath).encodedPath
    }
    /**
     * If internet connection is available, load all surveys and save it in the local database.
     * Also load and save the associated images in the internal storage.
     */
    @WorkerThread
    suspend fun loadSurveys(context: Context) {
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
            for (surveySectionJson: SurveyHeaderJson.SurveySectionJson in surveyHeaderJson.surveySection) {
                //save SurveySections
                surveySectionDao.insert(
                    SurveySection(
                        id = surveySectionJson.id,
                        sectionName = surveySectionJson.sectionName,
                        sectionSubheading = surveySectionJson.sectionSubheading,
                        sectionTitle = surveySectionJson.sectionTitle,
                        surveyHeaderId = surveyHeaderJson.id
                    )
                )
                //loop through questions
                for (questionJson: SurveyHeaderJson.SurveySectionJson.QuestionJson in surveySectionJson.questions) {

                    //check if image exists. Room returns count, less memory than query the object.
                    if (questionImageDao.exists(questionJson.questionImage.id) == 0) {
                        //load bitmap from url
                        val futureTarget =
                            Glide.with(context).asBitmap().load(questionJson.questionImage.url)
                                .submit()
                        val bitmap = futureTarget.get()
                        //write bitmap to file
                        val absolutePath = bitmapToFile(bitmap, context)
                        Glide.with(context).clear(futureTarget)

                        //get file path
                        //Save Images
                        questionImageDao.insert(
                            QuestionImage(
                                id = questionJson.questionImage.id,
                                url = questionJson.questionImage.url,
                                path = absolutePath
                            )
                        )
                    }
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
                            questionImageId = questionJson.questionImage.id,
                            questionName = questionJson.questionName,
                            questionSubtext = questionJson.questionSubtext,
                            surveySectionId = surveySectionJson.id
                        )
                    )
                    //Loop through question options
                    for (questionOptionJson: SurveyHeaderJson.SurveySectionJson.QuestionJson.QuestionOptionJson in questionJson.questionOptions) {
                        //check if option choice exist
                        if (optionChoiceDao.exists(questionOptionJson.optionChoice.id) == 0) {
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
        val allQuestions = questionDao.getAll()
        print(allQuestions.toString())
    }

    /**
     * Provide a list of all in database stored survey headers.
     */
    fun getSurveyHeaders(): LiveData<List<SurveyHeader>> {
        return surveyHeaderDao.getAll()
    }


}