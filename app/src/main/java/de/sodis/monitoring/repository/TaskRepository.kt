package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.IntervieweeTechnologyDao
import de.sodis.monitoring.db.dao.SurveyHeaderDao
import de.sodis.monitoring.db.dao.TaskDao
import de.sodis.monitoring.db.entity.Task

class TaskRepository(
    private val taskDao: TaskDao,
    private val monitoringApi: MonitoringApi
) {
    suspend fun downloadTasks() {
        val response = monitoringApi.getTasks()
        response.forEach { apiTask ->
            taskDao.insert(
                Task(
                    id = apiTask.id,
                    name = apiTask.name,
                    surveyHeaderId = apiTask.surveyHeaderJson?.let { it.id },
                    completedOn = apiTask.completedOn,
                    intervieweeTechnologyId = apiTask.intervieweeTechnology.id,
                    type = apiTask.type
                )
            )
        }
    }

    suspend fun getTasks() {
        taskDao.getAll()
    }

    suspend fun getAllTasksByInterviewee(intervieweeId: Int): List<Task> {
        return taskDao.getTasksByInterviewee(intervieweeId)
    }


}