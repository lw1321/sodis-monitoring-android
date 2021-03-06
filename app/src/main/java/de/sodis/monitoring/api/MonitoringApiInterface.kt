package de.sodis.monitoring.apiCompletedSurveyJson

import de.sodis.monitoring.api.models.*
import de.sodis.monitoring.db.entity.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MonitoringApiInterface {
    @GET("surveys")
    suspend fun getAllSurveys(): List<SurveyJson>

    @GET("survey-sections")
    suspend fun getAllSections(): List<SectionJson>

    @GET("questions")
    suspend fun getAllQuestions(): List<QuestionJson>

    @GET("option-choices")
    suspend fun getAllOptionChoices(): List<OptionChoice>

    @GET("interviewees")
    suspend fun getAllInterviewees(): List<IntervieweeJson>

    @POST("users/register")
    suspend fun registerUser(@Body user: User): User

    @POST("completed-surveys")
    suspend fun postCompletedSurveys(@Body completedSurvey: CompletedSurveyJson): Response<CompletedSurvey>

    @GET("question-images")
    suspend fun getAllQuestionImages(): List<QuestionImage>

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
    ): Response<Interviewee>

    @GET("projects")
    suspend fun getAllProjects(): List<Project>

    @GET("villages")
    suspend fun getAllVillages(): List<Village>

    @POST("interviewees")
    suspend fun postInterviewee(@Body interviewee: IntervieweeJson): Response<IntervieweeJson>

    @POST("answers/")
    suspend fun postAnswer(@Body answerList: AnswerJson): Response<Answer>

    @Multipart
    @POST("answers/{answerId}/images")
    suspend fun postAnswerImage(
            @Part
            image: MultipartBody.Part, @Path(
                    value = "answerId",
                    encoded = false
            ) answerId: String
    ): Response<Answer>

    @GET("questions/input-types/")
    suspend fun getAllInputTypes(): List<InputType>


}