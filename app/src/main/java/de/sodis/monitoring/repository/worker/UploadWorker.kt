package de.sodis.monitoring.repository.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.repository.QuestionRepository

class UploadWorker(var appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val questionRepository = QuestionRepository(
            questionDao = MonitoringDatabase.getDatabase(appContext).questionDao(),
            questionOptionDao = MonitoringDatabase.getDatabase(appContext).questionOptionDao(),
            questionImageDao = MonitoringDatabase.getDatabase(appContext).questionImageDao(),
            answerDao = MonitoringDatabase.getDatabase(appContext).answerDao(),
            monitoringApi = MonitoringApi()
        )
        questionRepository.uploadQuestions()
        return Result.success()
    }
}