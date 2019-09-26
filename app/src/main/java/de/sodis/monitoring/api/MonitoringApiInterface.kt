package de.sodis.monitoring.api

import de.sodis.monitoring.api.model.SurveyHeaderJson
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface MonitoringApiInterface {
    @GET("surveys")
    fun getAllSurveysAsync(): Deferred<Response<List<SurveyHeaderJson>>>
}