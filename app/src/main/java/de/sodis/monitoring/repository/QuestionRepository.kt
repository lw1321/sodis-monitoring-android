package de.sodis.monitoring.repository

import android.content.Context
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.*
import de.sodis.monitoring.db.entity.*
import id.zelory.compressor.Compressor
import java.io.File


class QuestionRepository(
    private val answerDao: AnswerDao,
    private val completedSurveyDao: CompletedSurveyDao,
    private val monitoringApi: MonitoringApi
) {


}



