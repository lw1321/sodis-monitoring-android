package de.sodis.monitoring.api

import de.sodis.monitoring.api.model.*
import de.sodis.monitoring.db.entity.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface MonitoringApiInterface {
    @GET("surveys")
    suspend fun getAllSurveys(): List<SurveyHeaderJson>

    @GET("interviewees")
    suspend fun getAllInterviewees(): List<IntervieweeJson>

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
            ) intervieweeId: String
    ): IntervieweeJson

    @GET("villages")
    suspend fun getAllVillages(): List<Village>

    @POST("interviewees")
    suspend fun postInterviewee(@Body interviewee: CompletedSurveyJson.Interviewee): Interviewee

    @Multipart
    @POST("answers/{answerId}/images")
    suspend fun postAnswerImage(
            @Part
            image: MultipartBody.Part, @Path(
                    value = "answerId",
                    encoded = false
            ) answerId: String
    ): Answer
}