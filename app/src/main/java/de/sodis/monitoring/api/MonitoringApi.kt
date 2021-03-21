package de.sodis.monitoring.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import de.sodis.monitoring.Config
import de.sodis.monitoring.api.models.QuestionJson
import de.sodis.monitoring.apiCompletedSurveyJson.MonitoringApiInterface
import de.sodis.monitoring.db.entity.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
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
                .baseUrl(Config.MONITORING_API_DEV)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClient)
                .build()
        monitoringApi = retrofit.create(MonitoringApiInterface::class.java)
    }

    suspend fun getSurveys(): List<SurveyHeader> {
        return monitoringApi.getAllSurveys()
    }
    suspend fun getSections(): List<SurveySection> {
        return monitoringApi.getAllSections()
    }

    suspend fun getOptionChoices(): List<OptionChoice> {
        return monitoringApi.getAllOptionChoices()
    }
    suspend fun getInputTypes(): List<InputType> {
        return monitoringApi.getAllInputTypes()
    }

    suspend fun getInterviewees(): List<Interviewee> {
        return monitoringApi.getAllInterviewees()
    }

    suspend fun registerUser(user: User): User {
        return monitoringApi.registerUser(user)
    }

    suspend fun postCompletedSurveys(completedSurveyList: List<CompletedSurvey>): List<CompletedSurvey> {
        return monitoringApi.postCompletedSurveys(completedSurveyList)
    }

    suspend fun getQuestionImages(): List<QuestionImage> {
        return monitoringApi.getAllQuestionImages()
    }

    suspend fun getStats(): Stats {
        return monitoringApi.getStats()
    }

    suspend fun postIntervieweImage(imagePath: String?, intervieweeId: String): Interviewee {
        val file = File(imagePath)
        val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body =
                MultipartBody.Part.createFormData("image", file.name, requestFile)
        val ret = monitoringApi.postIntervieweeImage(body, intervieweeId)
        return ret
    }

    suspend fun getAllVillages(): List<Village> {
        return monitoringApi.getAllVillages()
    }
    suspend fun getAllProjects(): List<Project> {
        return monitoringApi.getAllProjects()
    }

    suspend fun postInterviewee(interviewee: Interviewee): Interviewee {
        return monitoringApi.postInterviewee(interviewee)
    }

    suspend fun postAnswerImage(id: String, file: File): Answer {

        val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body =
                MultipartBody.Part.createFormData("image", file.name, requestFile)
        return monitoringApi.postAnswerImage(body, id)
    }

    suspend fun postAnswers(answers: List<Answer>): List<Answer> {
        return monitoringApi.postAnswer(answers)
    }
    suspend fun getAllQuestions(): List<QuestionJson> {
        return monitoringApi.getAllQuestions()
    }
}