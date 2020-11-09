package de.sodis.monitoring.api

import de.sodis.monitoring.api.model.*
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.Stats
import de.sodis.monitoring.db.entity.User
import de.sodis.monitoring.db.entity.Village
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MonitoringApiInterface {
    @GET("surveys")
    suspend fun getAllSurveys(): List<SurveyHeaderJson>

    @GET("interviewees")
    suspend fun getAllInterviewees(): List<IntervieweeJson>

    @GET("tasks")
    suspend fun getAllTasks(): List<TaskJson>

    @PUT("tasks/{taskId}")
    suspend fun updateTask(
        @Path(
            value = "taskId",
            encoded = false
        ) taskId: Int, @Body task: TaskJson
    ): TaskJson

    @POST("tasks")
    suspend fun createTask(@Body task: TaskJson): TaskJson

    @POST("users/register")
    suspend fun registerUser(@Body user: User): User

    @GET("myself")
    suspend fun getMyself(): User

    @GET("users")
    suspend fun getAllUsers(): List<User>

    @POST("completed-surveys")
    suspend fun postCompletedSurveys(@Body completedSurveyJson: List<CompletedSurveyJson>): List<CompletedSurveyJson>

    @GET("question-images")
    suspend fun getAllQuestionImages(): List<SurveyHeaderJson.SurveySectionJson.QuestionJson.QuestionImageJson>

    @GET("stats")
    suspend fun getStats(): Stats

    @Multipart
    @POST("interviewees/{intervieweeId}/image")
    suspend fun postIntervieweeImage(
        @Part
        image: MultipartBody.Part, @Path(
            value = "intervieweeId",
            encoded = false
        ) intervieweeId: Int
    ): IntervieweeJson

    @GET("villages")
    suspend fun getAllVillages(): List<Village>

    @POST
    suspend fun postInterviewee(@Body interviewee: Interviewee): Interviewee
}