package de.sodis.monitoring.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import de.sodis.monitoring.Config
import de.sodis.monitoring.api.model.AnswerJson
import de.sodis.monitoring.api.model.SurveyHeaderJson
import de.sodis.monitoring.db.entity.Interviewee
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor



class MonitoringApi {
    private var monitoringApi: MonitoringApiInterface

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(Config.MONITORING_API_DEV)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        monitoringApi = retrofit.create(MonitoringApiInterface::class.java)
    }

    fun getSurveys(): Call<List<SurveyHeaderJson>> {
        return monitoringApi.getAllSurveys()
    }

    fun getInterviewees(): Call<List<Interviewee>> {
        return monitoringApi.getAllInterviewees()
    }
    fun postAnswers(answers: List<AnswerJson>): Call<List<AnswerJson>> {
        return monitoringApi.postAnswers(answers)
    }
}