package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.MonitoringDatabase
import de.sodis.monitoring.db.entity.Task
import de.sodis.monitoring.db.entity.TodoPoint
import de.sodis.monitoring.repository.TaskRepository
import de.sodis.monitoring.repository.TodoPointRepository

class TodoPointModel(application: Application) : AndroidViewModel(application)  {
    var todoPointList: LiveData<List<TodoPoint>>

    val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    val todoPointRepository=
        TodoPointRepository(
            todoPointDao = monitoringDatabase.todoPointDao(),
            monitoringApi = MonitoringApi()
        )

    init {
        todoPointList= todoPointRepository.getAllTodoPoints()
    }


}