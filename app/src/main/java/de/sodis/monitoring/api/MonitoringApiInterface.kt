package de.sodis.monitoring.api

import de.sodis.monitoring.api.model.*
import de.sodis.monitoring.db.entity.User
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

    @POST("users/register")
    suspend fun registerUser(@Body user: User): User

    @GET("myself")
    suspend fun getMyself(): User

    @GET("users")
    suspend fun getAllUsers(): List<User>
}