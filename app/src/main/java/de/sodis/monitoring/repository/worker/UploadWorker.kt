package de.sodis.monitoring.repository.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.crashlytics.android.Crashlytics
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.repository.QuestionRepository

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    //TODO upload new user data
    override suspend fun doWork(): Result {
        val questionRepository = QuestionRepository(
            questionDao = MonitoringDatabase.getDatabase(applicationContext).questionDao(),
            questionOptionDao = MonitoringDatabase.getDatabase(applicationContext).questionOptionDao(),
            questionImageDao = MonitoringDatabase.getDatabase(applicationContext).questionImageDao(),
            answerDao = MonitoringDatabase.getDatabase(applicationContext).answerDao(),
            optionChoiceDao = MonitoringDatabase.getDatabase(applicationContext).optionChoiceDao(),
            completedSurveyDao = MonitoringDatabase.getDatabase(applicationContext).completedSurveyDao(),
            monitoringApi = MonitoringApi()
        )
        return try {
            questionRepository.uploadQuestions()
            Result.success()
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Result.failure()
        }
    }
}