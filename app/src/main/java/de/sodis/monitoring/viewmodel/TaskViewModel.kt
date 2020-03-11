package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Interviewee
import de.sodis.monitoring.db.entity.SurveyHeader
import de.sodis.monitoring.db.entity.Village
import de.sodis.monitoring.db.response.IntervieweeDetail
import de.sodis.monitoring.repository.IntervieweeRepository
import de.sodis.monitoring.repository.SurveyRepository
import androidx.lifecycle.MutableLiveData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.lifecycle.viewModelScope
import de.sodis.monitoring.db.dao.TaskDao
import de.sodis.monitoring.db.entity.Task
import de.sodis.monitoring.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TaskViewModel(application: Application) : AndroidViewModel(application) {

    var taskList: LiveData<List<Task>>

    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    private val taskRepository=
        TaskRepository(
            taskDao = monitoringDatabase.taskDao(),
            monitoringApi = MonitoringApi()
        )

    init {
        taskList= taskRepository.getTasks()
    }

}
