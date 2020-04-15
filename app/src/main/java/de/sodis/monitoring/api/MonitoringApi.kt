package de.sodis.monitoring.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import de.sodis.monitoring.Config
import de.sodis.monitoring.api.model.*
import de.sodis.monitoring.db.entity.Interviewee
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import io.fabric.sdk.android.services.settings.IconRequest.build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response


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

    suspend fun postAnswers(answers: List<AnswerJson>): List<AnswerJson> {
        return monitoringApi.postAnswers(answers)
    }

    suspend fun getTasks(): List<TaskJson> {
        return monitoringApi.getAllTasks()
    }

    suspend fun updateTask(task:TaskJson): TaskJson {
        return monitoringApi.updateTask(task.id, task)
    }

    suspend fun createTask(task:TaskJson): TaskJson {
        return monitoringApi.createTask(task)
    }

    suspend fun registerUser(userRegister: UserRegister): UserRegister {
        return monitoringApi.registerUser(userRegister)
    }
}