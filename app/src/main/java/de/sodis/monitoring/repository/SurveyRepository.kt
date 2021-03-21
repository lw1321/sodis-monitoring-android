package de.sodis.monitoring.repository

import android.content.Context
import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.CompletedSurveyDetail
import de.sodis.monitoring.db.response.CompletedSurveyOverview
import id.zelory.compressor.Compressor
import java.io.File


class SurveyRepository(
    private val surveyHeaderDao: SurveyHeaderDao,
    private val surveySectionDao: SurveySectionDao,
    private val questionDao: QuestionDao,
    private val inputTypeDao: InputTypeDao,
    private val optionChoiceDao: OptionChoiceDao,
    private val questionOptionDao: QuestionOptionDao,
    private val questionImageDao: QuestionImageDao,
    private val answerDao: AnswerDao,
    private val completedSurveyDao: CompletedSurveyDao,
    private val monitoringApi: MonitoringApi
) {

    suspend fun syncSurveys() {
        val surveys = monitoringApi.getSurveys()
        surveys.forEach {
            surveyHeaderDao.insert(it)
        }
    }

    suspend fun syncSections() {
        val sections = monitoringApi.getSections()
        sections.forEach {
            surveySectionDao.insert(it)
        }
    }

    suspend fun syncQuestions() {
        syncInputTypes()
        syncOptionChoices()
        syncQuestionImages()

        val questions = monitoringApi.getAllQuestions()
        questions.forEach { question ->
            questionDao.insert(
                Question(
                    id = question.id,
                    dependentQuestionId = question.dependentQuestionId,
                    dependentQuestionOptionId = question.dependentQuestionOptionId,
                    inputTypeId = question.inputType.id,
                    questionImageId = question.questionImage?.id,
                    questionName = question.questionName,
                    surveySectionId = question.surveySection.id
                )
            )
            question.questionOptions.forEach { questionOption ->
                questionOptionDao.insert(
                    QuestionOption(
                        id = questionOption.id,
                        questionId = question.id,
                        optionChoiceId = questionOption.optionChoice.id
                    )
                )
            }

        }
    }

    private suspend fun syncInputTypes() {
        val inputTypes = monitoringApi.getInputTypes()
        inputTypes.forEach {
            inputTypeDao.insert(it)
        }
    }

    private suspend fun syncOptionChoices() {
        val optionChoice = monitoringApi.getOptionChoices()
        optionChoice.forEach {
            optionChoiceDao.insert(optionChoice = it)
        }
    }

    private suspend fun syncQuestionImages() {
        val questionImages = monitoringApi.getQuestionImages()
        questionImages.forEach {
            questionImageDao.insert(questionImage = it)
        }

    }


    /**
     * Provide a list of all in database stored survey headers.
     */
    fun getSurveyHeadersFilteredTechnology(technologyId: Int): LiveData<List<SurveyHeader>> {
        return surveyHeaderDao.getAllFilteredTechnology(technologyId)
    }

    fun getSurveyHeadersFilteredTechnologySynchronous(technologyID: Int): List<SurveyHeader> {
        return surveyHeaderDao.getAllFilteredTechnologySync(technologyID)
    }

    /**
     * If internet connection is available, load all question images
     */
    suspend fun storeImages(context: Context) {
        val allNotDownloadedList = questionImageDao.getAllNotDownloaded()
        //loop through surveys
        for (questionImage: QuestionImage in allNotDownloadedList) {
            //save survey Header
            //store
            val absolutePath = Utils.urlToFile(questionImage.url, context)
            questionImage.path = absolutePath
            //Save Images
            questionImageDao.update(
                questionImage
            )
        }
    }


    /**
     * Save questions in loval database, also try to upload them..Also save if the upload was successfully
     */
    fun saveQuestions(
        answerMap: MutableMap<Int, Answer>,
        completedSurvey: CompletedSurvey
    ) {
        completedSurveyDao.insert(completedSurvey)

        for ((_, v) in answerMap) {
            v.completedSurveyId = completedSurvey.id
            answerDao.insert(v)
        }
    }

    suspend fun syncCompletedSurveys() {
        // TODO implement after server endpoints are adjust
        val surveysToSync = completedSurveyDao.getAllUnsubmitted()
        monitoringApi.postCompletedSurveys(surveysToSync)
    }

    suspend fun syncAnswers() {
        //upload answers
        val answersToSync = answerDao.getAllNotSubmitted()
        monitoringApi.postAnswers(answersToSync)
        answersToSync.forEach { answer->
            answer.submitted = true
            answerDao.update(answer)
        }
    }

    suspend fun syncAnswerImages(applicationContext: Context) {
        //upload images
        //check for not uploaded images where the answers are already synced.
        val answerImagesToSync = answerDao.getAllImageNotSynced()
        //upload the images
        answerImagesToSync.forEach { answer ->
            //Compress File
            //TODO compression before saving the image localy
            val compressedImageFile =
                Compressor.compress(applicationContext, File(answer.imagePath!!))
            monitoringApi.postAnswerImage(answer.id, compressedImageFile)
            answer.imageSynced = true
            answerDao.update(answer)
        }
    }


    fun getCompletedSurveys(): LiveData<List<CompletedSurveyOverview>> {
        return completedSurveyDao.getAll()
    }

    fun getCompletedSurveysSorted(): LiveData<List<CompletedSurveyOverview>> {
        return completedSurveyDao.getAllSorted()
    }

    fun getCompletedSurvey(completedSurveyId: String): List<CompletedSurveyDetail> {
        return completedSurveyDao.getAnswers(completedSurveyId)
    }

}