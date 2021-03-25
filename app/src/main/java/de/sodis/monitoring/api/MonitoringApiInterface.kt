package de.sodis.monitoring.apiCompletedSurveyJson

import de.sodis.monitoring.api.models.IntervieweeJson
import de.sodis.monitoring.api.models.QuestionJson
import de.sodis.monitoring.api.models.SectionJson
import de.sodis.monitoring.api.models.SurveyJson
import de.sodis.monitoring.db.entity.*
import okhttp3.MultipartBody
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
    suspend fun postCompletedSurveys(@Body completedSurveyList: List<CompletedSurvey>): List<CompletedSurvey>

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
    ): Interviewee

    @GET("projects")
    suspend fun getAllProjects(): List<Project>

    @GET("villages")
    suspend fun getAllVillages(): List<Village>

    @POST("interviewees")
    suspend fun postInterviewee(@Body interviewee: IntervieweeJson): IntervieweeJson

    @POST("answers/")
    suspend fun postAnswer(@Body answerList: List<Answer>): List<Answer>

    @Multipart
    @POST("answers/{answerId}/images")
    suspend fun postAnswerImage(
            @Part
            image: MultipartBody.Part, @Path(
                    value = "answerId",
                    encoded = false
            ) answerId: String
    ): Answer

    @GET("questions/input-types/")
    suspend fun getAllInputTypes(): List<InputType>


}