package de.sodis.monitoring.repository.worker

import com.google.firebase.crashlytics.FirebaseCrashlytics
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.QuestionImage
import de.sodis.monitoring.repository.*
import java.util.*


class DownloadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val Progress = "Progress"
    }

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
                userDao = monitoringDatabase.userDao()
            )
        val userRepository =
            UserRepository(
                monitoringApi = MonitoringApi(),
                userDao = monitoringDatabase.userDao()
            )
        val questionImageRepository = QuestionImageRepository(
            monitoringApi = MonitoringApi(),
            questionImageDao = monitoringDatabase.questionImageDao()
        )

        val statsRepository = StatsRepository(
            monitoringApi = MonitoringApi(),
            statsDao = monitoringDatabase.statsDao()
        )

        return try {
            val progressStarting = workDataOf(Progress to 0)
            val progress20 = workDataOf(Progress to 20)
            val progress40 = workDataOf(Progress to 40)
            val progress60 = workDataOf(Progress to 60)
            val progress80 = workDataOf(Progress to 80)
            val progressFinished = workDataOf(Progress to 100)
            setProgress(progressStarting)
            //check if there are new data
            if (statsRepository.dataUpdateAvailable()) {
                //ok cool there is new data. Let's sync it!
                userRepository.loadAllUsers()
                setProgress(progress20)
                intervieweeRepository.loadAll()
                setProgress(progress40)
                questionImageRepository.downloadMetaData()
                setProgress(progress60)
                surveyRepository.loadSurveys()
                setProgress(progress80)
                questionImageRepository.downloadQuestionImages(applicationContext)
                setProgress(progressFinished)
                //taskRepository.downloadTasks() //just offline tasks for now
                statsRepository.updateLastSyncTime()
                Result.success()
            }
            //Local data is already up to date!
            setProgress(progressFinished)
            Result.success()
        } catch (e: Exception) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log(e.localizedMessage)
            Result.failure()
        }
    }
}