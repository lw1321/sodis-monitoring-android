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
        val db = MonitoringDatabase.getDatabase(applicationContext)
        val surveyRepository =
            SurveyRepository(
                inputTypeDao = db.inputTypeDao(),
                optionChoiceDao = db.optionChoiceDao(),
                questionDao = db.questionDao(),
                questionOptionDao = db.questionOptionDao(),
                surveyHeaderDao = db.surveyHeaderDao(),
                surveySectionDao = db.surveySectionDao(),
                questionImageDao = db.questionImageDao(),
                answerDao =  db.answerDao(),
                completedSurveyDao = db.completedSurveyDao(),
                monitoringApi = MonitoringApi()
            )

        val placesRepository =
            PlaceRepository(
                intervieweeDao = db.intervieweeDao(),
                monitoringApi = MonitoringApi(),
                villageDao = db.villageDao(),
                userDao = db.userDao()
            )
        val projectRepository =
            ProjectRepository(
                monitoringApi = MonitoringApi(),
                projectDao = db.projectDao()
            )

        val statsRepository = StatsRepository(
            monitoringApi = MonitoringApi(),
            statsDao = db.statsDao()
        )

        return try {
            setProgress(progress(0))
            //check if there are new data
            if (statsRepository.dataUpdateAvailable()) {
                //ok cool there is new data. Let's sync it!
                //LOAD PROJECTS/
                projectRepository.loadProjects()
                // LOAD PLACES
                placesRepository.loadVillages()
                placesRepository.loadFamilies()
                //LOAD SURVEYS
                surveyRepository.syncSurveys()
                surveyRepository.syncSections()
                surveyRepository.syncQuestions()
                surveyRepository.storeImages(applicationContext)
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