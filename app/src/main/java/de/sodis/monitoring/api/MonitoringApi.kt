package de.sodis.monitoring.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import de.sodis.monitoring.Config
import de.sodis.monitoring.api.model.CompletedSurveyJson
import de.sodis.monitoring.api.model.IntervieweeJson
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.api.model.TaskJson
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

    suspend fun getSurveys(): List<SurveyHeaderJson> {
        return monitoringApi.getAllSurveys()
    }

    suspend fun getInterviewees(): List<IntervieweeJson> {
        return monitoringApi.getAllInterviewees()
    }

    suspend fun getTasks(): List<TaskJson> {
        return monitoringApi.getAllTasks()
    }

    suspend fun updateTask(task: TaskJson): TaskJson {
        return monitoringApi.updateTask(task.id, task)
    }

    suspend fun createTask(task: TaskJson): TaskJson {
        return monitoringApi.createTask(task)
    }

    suspend fun registerUser(user: User): User {
        return monitoringApi.registerUser(user)
    }

    suspend fun getAllUsers(): List<User> {
        return monitoringApi.getAllUsers()
    }

    suspend fun getMyself(): User {
        return monitoringApi.getMyself()
    }

    suspend fun postCompletedSurveys(completedSurveyJsonList: List<CompletedSurveyJson>): List<CompletedSurveyJson> {
        return monitoringApi.postCompletedSurveys(completedSurveyJsonList)
    }

    suspend fun getQuestionImages(): List<SurveyHeaderJson.SurveySectionJson.QuestionJson.QuestionImageJson> {
        return monitoringApi.getAllQuestionImages()
    }

    suspend fun getStats(): Stats {
        return monitoringApi.getStats()
    }

    suspend fun postIntervieweImage(imagePath: String?, intervieweeId: String): IntervieweeJson {
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

    suspend fun postInterviewee(interviewee: CompletedSurveyJson.Interviewee): Interviewee {
        return monitoringApi.postInterviewee(interviewee)
    }

    suspend fun postIntervieweeTechnology(intervieweeId: String, intervieweeTechnology: IntervieweeJson.IntervieweeTechnology): IntervieweeJson.IntervieweeTechnology {
        return monitoringApi.postIntervieweeTechnology(intervieweeId, intervieweeTechnology)
    }

    suspend fun postAnswerImage(id: String, file: File): Answer {

        val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body =
                MultipartBody.Part.createFormData("image", file.name, requestFile)
        return monitoringApi.postAnswerImage(body, id)
    }
}