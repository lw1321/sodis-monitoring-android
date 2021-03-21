package de.sodis.monitoring.repository.worker

import android.content.Context
import android.text.style.ReplacementSpan
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.PlaceRepository
import de.sodis.monitoring.repository.QuestionRepository
import de.sodis.monitoring.repository.SurveyRepository

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
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
                answerDao =  db.answerDao(),
                completedSurveyDao = db.completedSurveyDao(),
                monitoringApi = MonitoringApi()
            )

        return try {
            //SYNC Places
            placeRepository.syncInterviewee()
            placeRepository.uploadProfilPictures()
            //SYNC collected SURVEY DATA
            surveyRepository.syncCompletedSurveys()
            surveyRepository.syncAnswers()
            surveyRepository.syncAnswerImages(applicationContext)
            Result.success()
        } catch (e: Exception) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log(e.localizedMessage)
            Result.failure()
        }
    }
}