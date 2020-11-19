package de.sodis.monitoring.repository.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.QuestionRepository

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val db = MonitoringDatabase.getDatabase(applicationContext)
        val questionRepository = QuestionRepository(
            questionDao = MonitoringDatabase.getDatabase(applicationContext).questionDao(),
            questionOptionDao = MonitoringDatabase.getDatabase(applicationContext)
                .questionOptionDao(),
            questionImageDao = MonitoringDatabase.getDatabase(applicationContext)
                .questionImageDao(),
            answerDao = MonitoringDatabase.getDatabase(applicationContext).answerDao(),
            optionChoiceDao = MonitoringDatabase.getDatabase(applicationContext).optionChoiceDao(),
            completedSurveyDao = MonitoringDatabase.getDatabase(applicationContext)
                .completedSurveyDao(),
            monitoringApi = MonitoringApi(),
            intervieweeTechnologyDao = MonitoringDatabase.getDatabase(applicationContext.applicationContext)
                .intervieweeTechnologyDao(),
            surveyHeaderDao = MonitoringDatabase.getDatabase(applicationContext.applicationContext)
                .surveyHeaderDao(),
            intervieweeDao =    MonitoringDatabase.getDatabase(applicationContext.applicationContext)
                .intervieweeDao()
        )
        val intervieweeRepository = IntervieweeRepository(
            intervieweeTechnologyDao = db.intervieweeTechnologyDao(),
            monitoringApi = MonitoringApi(),
            intervieweeDao = db.intervieweeDao(),
            technologyDao = db.technologyDao(),
            userDao = db.userDao(),
            villageDao = db.villageDao()
        )
        return try {
            questionRepository.uploadQuestions()
            intervieweeRepository.uploadProfilPictures()

            Result.success()
        } catch (e: Exception) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log(e.localizedMessage)
            Result.failure()
        }
    }
}