package de.sodis.monitoring.repository.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.crashlytics.FirebaseCrashlytics
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.repository.PlaceRepository
import de.sodis.monitoring.repository.SurveyRepository

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        const val Progress = "Progress"
    }

    override suspend fun doWork(): Result {
        val db = MonitoringDatabase.getDatabase(applicationContext)
        val placeRepository = PlaceRepository(
            monitoringApi = MonitoringApi(),
            intervieweeDao = db.intervieweeDao(),
            userDao = db.userDao(),
            villageDao = db.villageDao()
        )
        val surveyRepository =
            SurveyRepository(
                inputTypeDao = db.inputTypeDao(),
                optionChoiceDao = db.optionChoiceDao(),
                questionDao = db.questionDao(),
                questionOptionDao = db.questionOptionDao(),
                surveyHeaderDao = db.surveyHeaderDao(),
                surveySectionDao = db.surveySectionDao(),
                questionImageDao = db.questionImageDao(),
                answerDao = db.answerDao(),
                completedSurveyDao = db.completedSurveyDao(),
                monitoringApi = MonitoringApi()
            )

        return try {
            //SYNC Places
            setProgress(progress(0))
            placeRepository.syncInterviewee()
            //SYNC collected SURVEY DATA
            surveyRepository.syncCompletedSurveys()
            setProgress(progress(50))
            surveyRepository.syncAnswers()
            surveyRepository.syncAnswerImages(applicationContext)
            //SYNC interviewee images
            placeRepository.syncProfilPictures()
            setProgress(progress(100))
            Result.success()
        } catch (e: Exception) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log(e.localizedMessage)
            Result.failure()
        }
    }
    private fun progress(progressCount: Int): Data {
        return  workDataOf(DownloadWorker.Progress to progressCount)
    }
}