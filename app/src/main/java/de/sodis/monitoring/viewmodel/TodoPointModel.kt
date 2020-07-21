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

    var undoneTodoPointList: LiveData<List<TodoPoint>>

    var undoneTodoPointsByDueDate: LiveData<List<TodoPoint>>

    var undoneTodoPointsByFamily: LiveData<List<TodoPoint>>

    var undoneTodoPointsByVillage: LiveData<List<TodoPoint>>

    fun insertTodoPoint(todoPoint: TodoPoint) {
        todoPointRepository.insertTodoPoint(todoPoint)
    }

    fun updateTodoPoint(todoPoint: TodoPoint) {
        todoPointRepository.updateTodoPoint(todoPoint)
    }

    fun deleteTodoPoint(todoPoint: TodoPoint) {
        todoPointRepository.deleteTodoPoint(todoPoint)
    }

    val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)
    val todoPointRepository=
        TodoPointRepository(
            todoPointDao = monitoringDatabase.todoPointDao(),
            monitoringApi = MonitoringApi()
        )

    init {
        todoPointList= todoPointRepository.getAllTodoPoints()
        undoneTodoPointList = todoPointRepository.getUndoneTodoPoints()
        undoneTodoPointsByDueDate = todoPointRepository.getUndoneTodoPointsByDueDate()
        undoneTodoPointsByFamily = todoPointRepository.getUndoneTodoPointsByFamily()
        undoneTodoPointsByVillage = todoPointRepository.getUndoneTodoPointsByVillage()
    }


}