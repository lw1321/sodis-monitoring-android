package de.sodis.monitoring.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import de.sodis.monitoring.Config
import de.sodis.monitoring.api.model.*
import de.sodis.monitoring.db.entity.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient


class MonitoringApi {
    private var monitoringApi: MonitoringApiInterface

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(FirebaseUserIdTokenInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Config.MONITORING_API_TEST)
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

}