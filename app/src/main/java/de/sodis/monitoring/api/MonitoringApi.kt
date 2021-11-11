package de.sodis.monitoring.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import de.sodis.monitoring.Config
import de.sodis.monitoring.api.models.*
import de.sodis.monitoring.apiCompletedSurveyJson.MonitoringApiInterface
import de.sodis.monitoring.db.entity.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class MonitoringApi {
    private var monitoringApi: MonitoringApiInterface

    init {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(FirebaseUserIdTokenInterceptor())
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(Config.MONITORING_API_LIVE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClient)
                .build()
        monitoringApi = retrofit.create(MonitoringApiInterface::class.java)
    }

    suspend fun getSurveys(): List<SurveyJson> {
        return monitoringApi.getAllSurveys()
    }

    suspend fun getSections(): List<SectionJson> {
        return monitoringApi.getAllSections()
    }

    suspend fun getOptionChoices(): List<OptionChoice> {
        return monitoringApi.getAllOptionChoices()
    }

    suspend fun getInputTypes(): List<InputType> {
        return monitoringApi.getAllInputTypes()
    }

    suspend fun getInterviewees(): List<IntervieweeJson> {
        return monitoringApi.getAllInterviewees()
    }

    suspend fun registerUser(user: User): User {
        return monitoringApi.registerUser(user)
    }

    suspend fun postCompletedSurveys(completedSurvey: CompletedSurvey): Response<CompletedSurvey> {
        return monitoringApi.postCompletedSurveys(
                CompletedSurveyJson(
                        id = completedSurvey.id,
                        longitude = completedSurvey.longitude,
                        latitude = completedSurvey.latitude,
                        creationDate = completedSurvey.creationDate,
                        surveyHeader = CompletedSurveyJson.SurveyHeader(
                                id = completedSurvey.surveyHeaderId
                        ),
                        submitted = completedSurvey.submitted,
                        interviewee = CompletedSurveyJson.Interviewee(
                                id = completedSurvey.intervieweeId
                        )
                )
        )
    }

    suspend fun getQuestionImages(): List<QuestionImage> {
        return monitoringApi.getAllQuestionImages()
    }

    suspend fun getStats(): Stats {
        return monitoringApi.getStats()
    }

    suspend fun postIntervieweImage(imagePath: String, intervieweeId: String): Response<Interviewee> {
        val file = File(imagePath)
        val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body =
                MultipartBody.Part.createFormData("image", file.name, requestFile)
        return  monitoringApi.postIntervieweeImage(body, intervieweeId)
    }

    suspend fun getAllVillages(): List<Village> {
        return monitoringApi.getAllVillages()
    }

    suspend fun getAllProjects(): List<Project> {
        return monitoringApi.getAllProjects()
    }

    suspend fun postInterviewee(interviewee: IntervieweeJson): Response<IntervieweeJson> {
        return monitoringApi.postInterviewee(interviewee)
    }

    suspend fun postAnswerImage(id: String, file: File): Response<Answer> {

        val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body =
                MultipartBody.Part.createFormData("image", file.name, requestFile)
        return monitoringApi.postAnswerImage(body, id)
    }

    suspend fun postAnswers(answer: Answer): Response<Answer> {
        var answerJson = AnswerJson(
                id = answer.id,
                completedSurvey = AnswerJson.CompletedSurvey(
                        id = answer.completedSurveyId!!
                ),
                answerText = answer.answerText,
                question = AnswerJson.Question(
                        answer.questionId
                )
        )
        if (answer.questionOptionId != null) {
            answerJson.questionOption = AnswerJson.QuestionOption(
                    id = answer.questionOptionId
            )
        }
        return monitoringApi.postAnswer(
                answerJson
        )

    }

    suspend fun getAllQuestions(): List<QuestionJson> {
        return monitoringApi.getAllQuestions()
    }
}