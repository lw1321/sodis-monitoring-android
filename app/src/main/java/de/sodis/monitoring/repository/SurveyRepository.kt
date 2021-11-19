package de.sodis.monitoring.repository

import android.content.Context
import androidx.lifecycle.LiveData
import de.sodis.monitoring.Utils
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import de.sodis.monitoring.db.response.CompletedSurveyItem
import de.sodis.monitoring.db.response.QuestionItem
import de.sodis.monitoring.db.response.SurveyList
import id.zelory.compressor.Compressor
import java.io.File
import java.sql.Timestamp
import java.util.*


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
        surveys.forEach { survey ->
            surveyHeaderDao.insert(
                    SurveyHeader(
                            id = survey.id,
                            projectId = survey.project.id,
                            surveyName = survey.surveyName
                    )
            )
        }
    }

    suspend fun syncSections() {
        val sections = monitoringApi.getSections()
        sections.forEach { surveySection ->
            surveySectionDao.insert(
                    SurveySection(
                            id = surveySection.id,
                            sectionName = surveySection.sectionName,
                            surveyHeaderId = surveySection.surveyHeader.id
                    )
            )
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
    fun saveCompletedSurvey(
            surveyHeaderId: Int,
            answerMap: MutableMap<Int, Answer>,
            intervieweeId: String,
            latitude: Double?,
            longitude: Double?
    ) {

        val completedSurveyId = UUID.randomUUID().toString()

        completedSurveyDao.insert(
                CompletedSurvey(
                        id = completedSurveyId,
                        intervieweeId = intervieweeId,
                        surveyHeaderId = surveyHeaderId,
                        creationDate = Timestamp(System.currentTimeMillis()).toString(),
                        latitude = latitude,
                        longitude = longitude
                )
        )

        for ((_, v) in answerMap) {
            v.completedSurveyId = completedSurveyId
            answerDao.insert(v)
        }
    }

    suspend fun syncCompletedSurveys() {
        val surveysToSync = completedSurveyDao.getAllUnsubmitted()
        surveysToSync.forEach {
            val responsePostSurvey = monitoringApi.postCompletedSurveys(it)
            if (responsePostSurvey.isSuccessful) {
                it.submitted = true
                completedSurveyDao.update(it)
            }
        }
    }

    suspend fun syncAnswers() {
        //upload answers
        val answersToSync = answerDao.getAllNotSubmitted()
        answersToSync.forEach { answer ->
            val responsePostSurvey = monitoringApi.postAnswers(answer)
            if (responsePostSurvey.isSuccessful) {
                answer.submitted = true
                answerDao.update(answer)
            }
        }
    }

    suspend fun syncAnswerImages(applicationContext: Context) {
        //upload images
        //check for not uploaded images where the answers are already synced.
        val answerImagesToSync = answerDao.getNotSubmittedImages()
        //upload the images
        answerImagesToSync.forEach { answer ->
            //Compress File
            //TODO compression before saving the image localy
            val compressedImageFile =
                    Compressor.compress(applicationContext, File(answer.imagePath!!))
            val responsePostAnswerImage = monitoringApi.postAnswerImage(answer.id, compressedImageFile)
            if (responsePostAnswerImage.isSuccessful) {
                answer.imageSynced = true
                answerDao.update(answer)
            }
        }
    }

    fun getSurveyList(): LiveData<List<SurveyList>> {
        return surveyHeaderDao.getAllSurveys()
    }

    fun getQuestionList(surveyId: Int): List<QuestionItem> {
        return questionDao.getQuestionItems(surveyId)
    }

    fun getQuestionsDistinct(surveyId: Int): List<Int> {
        return questionDao.getBySurveyHeader(surveyId)
    }

    fun getAllCompletedSurveys(): LiveData<List<CompletedSurvey>> {
        return completedSurveyDao.getAll()
    }

    fun deleteAll() {
        answerDao.deleteAll()
        completedSurveyDao.deleteAll()
        questionOptionDao.deleteAll()
        optionChoiceDao.deleteAll()
        questionDao.deleteAll()
        inputTypeDao.deleteAll()
        questionImageDao.deleteAll()
        surveySectionDao.deleteAll()
        surveyHeaderDao.deleteAll()
    }

    fun getUnsyncedSurveyCountLive(): LiveData<Int> {
        return completedSurveyDao.getUnsubittedCountLive()
    }

    fun getUnsyncedSurveyCount(): Int {
        return completedSurveyDao.getUnsubittedCount()
    }

    fun getCompletedSurveyItem(): LiveData<List<CompletedSurveyItem>> {
        return completedSurveyDao.getAllSurveyItems()
    }

    fun getAllSurveyItemsUnsubmitted(): LiveData<List<CompletedSurveyItem>> {
        return completedSurveyDao.getAllSurveyItemsUnsubmitted()
    }


}