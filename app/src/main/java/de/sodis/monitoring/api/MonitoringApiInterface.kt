package de.sodis.monitoring.api

import de.sodis.monitoring.api.model.AnswerJson
import de.sodis.monitoring.api.model.IntervieweeJson
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.api.model.TaskJson
import de.sodis.monitoring.db.entity.Interviewee
import retrofit2.http.*

interface MonitoringApiInterface {
    @GET("surveys")
    suspend fun getAllSurveys(): List<SurveyHeaderJson>

    @GET("interviewees")
    suspend fun getAllInterviewees(): List<IntervieweeJson>

    @POST("answers")
    suspend fun postAnswers(@Body answers: List<AnswerJson>): List<AnswerJson>

    @GET("tasks")
    suspend fun getAllTasks(): List<TaskJson>

    @PUT("tasks/{taskId}")
    suspend fun updateTask(@Path(value = "taskId", encoded = false) taskId: Int, @Body task: TaskJson): TaskJson

    @POST("tasks")
    suspend fun createTask(@Body task: TaskJson): TaskJson

}