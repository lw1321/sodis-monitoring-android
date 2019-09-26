package de.sodis.monitoring.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import de.sodis.monitoring.Config
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.entity.SurveyHeader
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MonitoringApi {
    private var monitoringApi: MonitoringApiInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Config.MONITORING_API_DEV)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        monitoringApi = retrofit.create(MonitoringApiInterface::class.java)
    }

    fun getSurveysAsync(): Deferred<Response<List<SurveyHeaderJson>>> {
        return monitoringApi.getAllSurveysAsync()
    }
}