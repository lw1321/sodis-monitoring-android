package de.sodis.monitoring.api

import de.sodis.monitoring.api.model.AnswerJson
import de.sodis.monitoring.api.model.IntervieweeJson
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.entity.Interviewee
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MonitoringApiInterface {
    @GET("surveys")
    suspend fun getAllSurveys(): List<SurveyHeaderJson>

    @GET("interviewees")
    suspend fun getAllInterviewees(): List<IntervieweeJson>

    @POST("answers")
    suspend fun postAnswers(@Body answers: List<AnswerJson>): List<AnswerJson>
}