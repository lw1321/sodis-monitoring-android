package de.sodis.monitoring.repository.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.crashlytics.android.Crashlytics
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.SurveyRepository

class DownloadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val monitoringDatabase = MonitoringDatabase.getDatabase(applicationContext)
        val surveyRepository =
            SurveyRepository(
                inputTypeDao = monitoringDatabase.inputTypeDao(),
                optionChoiceDao = monitoringDatabase.optionChoiceDao(),
                questionDao = monitoringDatabase.questionDao(),
                questionImageDao = monitoringDatabase.questionImageDao(),
                questionOptionDao = monitoringDatabase.questionOptionDao(),
                surveyHeaderDao = monitoringDatabase.surveyHeaderDao(),
                surveySectionDao = monitoringDatabase.surveySectionDao(),
                monitoringApi = MonitoringApi()
            )
        val intervieweeRepository =
            IntervieweeRepository(
                intervieweeDao = monitoringDatabase.intervieweeDao(),
                monitoringApi = MonitoringApi(),
                villageDao = monitoringDatabase.villageDao()
            )

        return try {
            surveyRepository.loadSurveys(applicationContext)
            intervieweeRepository.loadAll()
            Result.success()
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Result.failure()
        }
    }
}