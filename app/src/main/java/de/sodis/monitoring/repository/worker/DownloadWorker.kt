package de.sodis.monitoring.repository.worker

import com.google.firebase.crashlytics.FirebaseCrashlytics
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.repository.*


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
                questionOptionDao = monitoringDatabase.questionOptionDao(),
                surveyHeaderDao = monitoringDatabase.surveyHeaderDao(),
                surveySectionDao = monitoringDatabase.surveySectionDao(),
                technologyDao = monitoringDatabase.technologyDao(),
                questionImageDao = monitoringDatabase.questionImageDao(),
                monitoringApi = MonitoringApi()
            )
        val intervieweeRepository =
            IntervieweeRepository(
                intervieweeDao = monitoringDatabase.intervieweeDao(),
                monitoringApi = MonitoringApi(),
                technologyDao = monitoringDatabase.technologyDao(),
                villageDao = monitoringDatabase.villageDao(),
                userDao = monitoringDatabase.userDao()
            )



        val statsRepository = StatsRepository(
            monitoringApi = MonitoringApi(),
            statsDao = monitoringDatabase.statsDao()
        )

        return try {
            setProgress(progress(0))
            //check if there are new data
            if (statsRepository.dataUpdateAvailable()) {
                //ok cool there is new data. Let's sync it!
                intervieweeRepository.loadVillages()
                setProgress(progress(15))
                intervieweeRepository.loadFamilies()
                setProgress(progress(30))
                surveyRepository.loadSurveys()
                setProgress(progress(45))
                surveyRepository.loadSections()
                setProgress(progress(60))
                surveyRepository.loadQuestions()
                setProgress(progress(75))
                surveyRepository.loadSurveys()
                setProgress(progress(90))
                surveyRepository.storeImages(applicationContext)
                //taskRepository.downloadTasks() //just offline tasks for now
                statsRepository.updateLastSyncTime()
                setProgress(progress(100))
                Result.success()
            }
            setProgress(progress(100))
            //Local data is already up to date!
            Result.success()
        } catch (e: Exception) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log(e.localizedMessage)
            Result.failure()
        }
    }

    private fun progress(progressCount: Int): Data {
        return  workDataOf(Progress to progressCount)
    }
}