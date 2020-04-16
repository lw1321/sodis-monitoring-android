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
import de.sodis.monitoring.repository.UserRepository


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
                userDao = monitoringDatabase.userDao(),
                taskDao = monitoringDatabase.taskDao()
            )
        val taskRepository =
            TaskRepository(
                taskDao = monitoringDatabase.taskDao(),
                monitoringApi = MonitoringApi()
            )
        val userRepository =
            UserRepository(
                monitoringApi = MonitoringApi(),
                userDao = monitoringDatabase.userDao()
            )

        return try {
            userRepository.loadAllUsers()
            intervieweeRepository.loadAll()
            surveyRepository.loadSurveys(applicationContext)
            taskRepository.downloadTasks() //todo
            Result.success()
        } catch (e: Exception) {
            Crashlytics.logException(e)
            Result.failure()
        }
    }
}