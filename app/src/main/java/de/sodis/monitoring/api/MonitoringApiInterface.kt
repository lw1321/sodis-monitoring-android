package de.sodis.monitoring.api

import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.entity.Interviewee
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface MonitoringApiInterface {
    @GET("surveys")
    fun getAllSurveysAsync(): Deferred<Response<List<SurveyHeaderJson>>>

    fun getAllIntervieweesAsync(): Deferred<Response<List<Interviewee>>>
}