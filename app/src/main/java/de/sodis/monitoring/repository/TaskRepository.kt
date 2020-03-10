package de.sodis.monitoring.repository

import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.IntervieweeTechnologyDao
import de.sodis.monitoring.db.dao.SurveyHeaderDao
import de.sodis.monitoring.db.dao.TaskDao

class TaskRepository(
    private val taskDao: TaskDao,
    private val intervieweeTechnologyDao: IntervieweeTechnologyDao,
    private val surveyHeaderDao: SurveyHeaderDao,
    private val monitoringApi: MonitoringApi
) {
}