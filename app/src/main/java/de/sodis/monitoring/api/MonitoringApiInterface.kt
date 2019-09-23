package de.sodis.monitoring.api

import de.sodis.monitoring.db.entity.SurveyHeader
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface MonitoringApiInterface {
    @GET("surveys")
    fun getAllSurveys(): Deferred<Response<List<SurveyHeader>>>
}