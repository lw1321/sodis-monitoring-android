package de.sodis.monitoring.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import de.sodis.monitoring.Config
import de.sodis.monitoring.api.model.AnswerJson
import de.sodis.monitoring.api.model.IntervieweeJson
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.api.model.TaskJson
import de.sodis.monitoring.db.entity.Interviewee
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MonitoringApi {
    private var monitoringApi: MonitoringApiInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Config.MONITORING_API_DEV)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
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
}