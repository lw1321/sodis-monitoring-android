package de.sodis.monitoring.repository

import androidx.lifecycle.LiveData
import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.TodoPointDao
import de.sodis.monitoring.db.entity.TodoPoint

class TodoPointRepository (private val todoPointDao: TodoPointDao, private val monitoringApi: MonitoringApi) {
    fun getAllTodoPoints():LiveData<List<TodoPoint>> {
        return todoPointDao.getAll()
    }

    fun getTodoPointsSortedByID():List<TodoPoint> {
        return todoPointDao.getTodoPointsSortedByInterviewee()
    }

    fun getTodoPointsByDueDate():List<TodoPoint> {
        return todoPointDao.getTodoPointsSortedByDueDate()
    }

    fun deleteTodoPoint(todoPoint: TodoPoint) {
        return todoPointDao.delete(todoPoint)
    }

    fun updateTodoPoint(todoPoint: TodoPoint):Int {
        return todoPointDao.update(todoPoint)
    }

    fun insertTodoPoint(todoPoint: TodoPoint) {
        return todoPointDao.insert(todoPoint)
    }


}