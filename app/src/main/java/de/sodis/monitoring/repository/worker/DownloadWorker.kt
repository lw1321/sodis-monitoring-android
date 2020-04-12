package de.sodis.monitoring.repository.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.crashlytics.android.Crashlytics
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.SurveyRepository
import de.sodis.monitoring.repository.TaskRepository


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
                technologyDao = monitoringDatabase.technologyDao(),
                monitoringApi = MonitoringApi()
            )
        val intervieweeRepository =
            IntervieweeRepository(
                intervieweeDao = monitoringDatabase.intervieweeDao(),
                monitoringApi = MonitoringApi(),
                technologyDao = monitoringDatabase.technologyDao(),
                intervieweeTechnologyDao = monitoringDatabase.intervieweeTechnologyDao(),
                villageDao = monitoringDatabase.villageDao(),
                sectorDao = monitoringDatabase.sectorDao(),
                localExpertDao = monitoringDatabase.localExpertDao(),
                taskDao = monitoringDatabase.taskDao()
            )
        val taskRepository =
            TaskRepository(
                taskDao = monitoringDatabase.taskDao(),
                monitoringApi = MonitoringApi()
            )

        return try {
            intervieweeRepository.loadAll()
            surveyRepository.loadSurveys(applicationContext)
            taskRepository.downloadTasks()
            Result.success()
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Result.failure()
        }
    }
}