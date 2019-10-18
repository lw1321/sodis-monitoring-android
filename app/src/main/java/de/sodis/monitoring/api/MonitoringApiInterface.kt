package de.sodis.monitoring.api

import de.sodis.monitoring.api.model.AnswerJson
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.entity.Interviewee
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MonitoringApiInterface {
    @GET("surveys")
    fun getAllSurveys(): Call<List<SurveyHeaderJson>>

    @GET("interviewees")
    fun getAllInterviewees(): Call<List<Interviewee>>

    @POST("answers")
    fun postAnswers(@Body answers: List<AnswerJson>): Call<List<AnswerJson>>
}